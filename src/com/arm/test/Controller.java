package com.arm.test;

import com.navin.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    Stage curr_stg;

    static class MODE{
        static int ADD_NODE = 1;
        static int DISPLACE_NODE = 2;
        static int REMOVE_NODE = 3;
        static int ADD_SHOP = 4;
        static int DISPLACE_SHOP = 5;
        static int REMOVE_SHOP = 6;
        static int VIEW_SHOP = 7;
        static int JOIN_NODE = 8;
        static int TRIM_PATH = 9;
        static int MAP_SHOP = 10;
        static int UNMAP_SHOP = 11;
    }
    int mode=0;
    @FXML Canvas map;
    GraphicsContext painter;
    @FXML Button add_node,displace_node,remove_node,set_bk,remove_bk;
    @FXML TabPane floor_tab;
    @FXML TextField floor_id,mall_name;
    @FXML Pane control_pane;

    Location curr_selected_node=null;
    Store curr_selected_store=null;

    Mall main_mall;
    ArrayList<Location> path_nodes;
    ArrayList<Store> stores;
    ArrayList<Pair> roads;


    boolean pairing_mode=false;
    Pair p;
    Object map_shop_temp_node_shop=null;

    File currentfile=null;
    Floor current_floor=null;
    Image currentBackground=null;

//TODO ________________________________________________________________________________________________________________________________________________

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        main_mall=new Mall();
        painter = map.getGraphicsContext2D();
        painter.setLineWidth(5);

        floor_id.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                current_floor.FloorID=floor_id.getText();
                syncTabView();
            }
        });

        mall_name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                if(main_mall==null)mall_name.setText("");
                else
                    main_mall.Name = mall_name.getText();
            }
        });
    }

    public void new_mall(ActionEvent actionEvent) {
        main_mall=null;
        main_mall = new Mall();
        control_pane.setDisable(true);
        syncTabView();
        current_floor=null;
        redraw();
    }

    public void new_floor(ActionEvent actionEvent) {
        if(main_mall.floors.size()==0)control_pane.setDisable(false);
        String fid = "UntitledFloor";int j = 1;
        String temp=fid;
        for(int i = 0; i< main_mall.floors.size();i++){
            if(main_mall.floors.get(i).FloorID.equals(temp))temp=fid+(++j);
        }
        Floor f = new Floor(temp,5000,5000);
        main_mall.floors.add(f);
        syncTabView();
        set_floor(f);
        for(int i = 0; i<floor_tab.getTabs().size();i++){
            if(floor_tab.getTabs().get(i).getText().equals(current_floor.FloorID))
                floor_tab.getSelectionModel().select(i);
        }
    }

    public void SaveAsFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("NAVin Maps (*.nvim)", "*.nvim");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(curr_stg);
        if(file.exists())file.delete();
        if(file != null){
            FileOutputStream fo = new FileOutputStream(file);
            ObjectOutputStream objstr = new ObjectOutputStream(fo);
            objstr.writeObject(main_mall);
            fo.close();
            currentfile=file;
        }
    }

    public void SaveFile(ActionEvent actionEvent) throws IOException {
        if(!currentfile.exists()||currentfile==null){SaveAsFile(null);return;}
        currentfile.delete();
        FileOutputStream fo = new FileOutputStream(currentfile);
        ObjectOutputStream objstr = new ObjectOutputStream(fo);
        objstr.writeObject(main_mall);
        fo.close();
    }

    public void openFile(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter efil = new FileChooser.ExtensionFilter("NAVin Maps","*.nvim");
        fileChooser.getExtensionFilters().add(efil);
        File f = fileChooser.showOpenDialog(curr_stg);
        FileInputStream fi = new FileInputStream(f);
        ObjectInputStream oi = new ObjectInputStream(fi);
        main_mall = (Mall) oi.readObject();
        syncTabView();
        if(main_mall.floors.size()>=1){
            control_pane.setDisable(false);
            set_floor(getFloor(floor_tab.getTabs().get(0).getText()));
        }
        currentfile=f;
        mall_name.setText(main_mall.Name);
    }

    public void quit(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void set_floor(Floor f){
        if(f==null)return;
        current_floor=f;
        this.path_nodes=f.path_nodes;
        this.roads=f.roads;
        this.stores=f.stores;
        this.map.setWidth(f.CanvasWidth);
        this.map.setHeight(f.CanvasHeight);
        floor_id.setText(f.FloorID);

        if(current_floor.background_map!=null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(current_floor.background_map);
            try {
                currentBackground = SwingFXUtils.toFXImage(ImageIO.read(bis), null);
            }catch(Exception e){}
        }
        else
            currentBackground=null;

        redraw();
    }

    private void syncTabView() {
        floor_tab.getTabs().removeAll(floor_tab.getTabs());
        Tab selectedtab=null;
        for(Floor floor : main_mall.floors) {
            Tab s = new Tab();
            s.setText(floor.FloorID);
            floor_tab.getTabs().add(s);

            s.setOnSelectionChanged(event -> {
                pairing_mode=false;p=null;
                set_floor(getFloor(s.getText()));
            });

            if(floor.equals(current_floor))
                selectedtab=s;
        }
        floor_tab.getSelectionModel().select(selectedtab);
    }

    private Floor getFloor(String s){
        for(int i=0; i<main_mall.floors.size();i++)
            if(main_mall.floors.get(i).FloorID.equals(s))
                return main_mall.floors.get(i);
        return null;
    }

    public void add_node_clicked(MouseEvent mouseEvent) {
        mode = MODE.ADD_NODE;
    }
    public void displace_node_clicked(MouseEvent mouseEvent) {
        mode=MODE.DISPLACE_NODE;
    }
    public void remove_node_clicked(MouseEvent mouseEvent) {
        mode = MODE.REMOVE_NODE;
    }
    public void add_store_clicked(MouseEvent mouseEvent){
        mode = MODE.ADD_SHOP;
    }
    public void displace_store_clicked(MouseEvent mouseEvent) { mode=MODE.DISPLACE_SHOP;}
    public void view_shop_clicked(MouseEvent mouseEvent) { mode=MODE.VIEW_SHOP;}
    public void remove_shop_clicked(MouseEvent mouseEvent) { mode = MODE.REMOVE_SHOP;}
    public void join_node_clicked(MouseEvent mouseEvent) { mode = MODE.JOIN_NODE;}
    public void trim_path_clicked(MouseEvent mouseEvent) { mode = MODE.TRIM_PATH;}
    public void map_shop(MouseEvent mouseEvent) { mode = MODE.MAP_SHOP;}
    public void Unmap_shop(MouseEvent mouseEvent) { mode=MODE.UNMAP_SHOP;}

    public void map_clicked(MouseEvent mouseEvent) throws IOException {
        if (mode == MODE.ADD_NODE) {
            if (get_Node(mouseEvent.getX(), mouseEvent.getY()) != null) return;
            path_nodes.add(new Location(mouseEvent.getX(), mouseEvent.getY()));
            painter.setFill(Color.BLACK);
            painter.fillOval(mouseEvent.getX() - 10, mouseEvent.getY() - 10, 20, 20);
        }
        if(mode==MODE.REMOVE_NODE){
            Location temp = get_Node(mouseEvent.getX(),mouseEvent.getY());
            if(temp==null)return;
            ArrayList<Pair> to_be_removed=new ArrayList<Pair>();
            for(Pair i : roads){
                if(i.A.equals(temp)||i.B.equals(temp))
                    to_be_removed.add(i);
            }
            for(Pair i : to_be_removed)
                roads.remove(i);
            path_nodes.remove(temp);
            redraw();
        }
        if(mode==MODE.ADD_SHOP){
            if (get_Store(mouseEvent.getX(), mouseEvent.getY()) != null) return;
            Store temp = new Store(mouseEvent.getX(),mouseEvent.getY());
            FXMLLoader l = new FXMLLoader(getClass().getResource("Add_Store.fxml"));
            Parent root = l.load();
            Stage add_stage = new Stage();
            add_stage.setTitle("Add Store");
            add_stage.initStyle(StageStyle.UNDECORATED);
            add_stage.setAlwaysOnTop(true);
            add_stage.setScene(new Scene(root, 500, 100));
            ((AddStore)l.getController()).setStore_and_get_stage(temp,add_stage);
            add_stage.showAndWait();
            if(temp.X!=-1) {
                painter.setFill(Color.valueOf(temp.color));
                painter.setStroke(Color.valueOf(temp.color));
                painter.fillOval(temp.X - 15, temp.Y - 15, 30, 30);
                stores.add(temp);
            }
        }
        if(mode==MODE.VIEW_SHOP){
            Store s = get_Store(mouseEvent.getX(), mouseEvent.getY());
            if (s == null) return;
            FXMLLoader l = new FXMLLoader(getClass().getResource("Add_Store.fxml"));
            Parent root = l.load();
            Stage add_stage = new Stage();
            add_stage.setTitle("VIew/Modify Store");
            add_stage.initStyle(StageStyle.UNDECORATED);
            add_stage.setAlwaysOnTop(true);
            add_stage.setScene(new Scene(root, 500, 100));
            ((AddStore)l.getController()).setStore_and_get_stage(s,add_stage);
            add_stage.showAndWait();

            redraw();
        }
        if(mode==MODE.REMOVE_SHOP){
            Store temp = get_Store(mouseEvent.getX(),mouseEvent.getY());
            if(temp==null)return;

            for(Location i : path_nodes){
                if(i.getStores().contains(temp))
                    i.getStores().remove(temp);
            }
            stores.remove(temp);
            redraw();
        }
        if(mode==MODE.JOIN_NODE){
            if(!pairing_mode) {
                Location pair_loc_1;
                pair_loc_1 = get_Node(mouseEvent.getX(),mouseEvent.getY());
                if(pair_loc_1==null)return;
                pairing_mode = true;
                p = new Pair();
                p.A=pair_loc_1;
                redraw();
                return;
            }
            if(pairing_mode){
                Location temp = get_Node(mouseEvent.getX(),mouseEvent.getY());
                if(temp==null){pairing_mode=false;p=null;redraw();return;}
                p.B=temp;
                if(!(pair_exists(p)||p.A.equals(p.B)))
                    roads.add(p);
                p = null;
                pairing_mode=false;
                redraw();
                return;
            }
        }

        if(mode==MODE.TRIM_PATH){
            roads.remove(getPair(mouseEvent.getX(),mouseEvent.getY()));
            redraw();
        }

        if(mode == MODE.MAP_SHOP){
            Location temp_loc = get_Node(mouseEvent.getX(),mouseEvent.getY());
            Store temp_store = get_Store(mouseEvent.getX(),mouseEvent.getY());
            if(temp_loc==null&&temp_store==null)return;

            if(map_shop_temp_node_shop==null) {
                System.out.println("First");
                if (temp_loc != null) map_shop_temp_node_shop = temp_loc;
                else if (temp_store != null) map_shop_temp_node_shop = temp_store;
                return;
            }
            else if(map_shop_temp_node_shop!=null){
                System.out.println("Second");
                if(temp_loc!=null&& map_shop_temp_node_shop instanceof Store){
                    temp_loc.stores.add((Store) map_shop_temp_node_shop);
                }
                else if(temp_store!=null&& map_shop_temp_node_shop instanceof Location){
                    ((Location) map_shop_temp_node_shop).stores.add(temp_store);
                }
                map_shop_temp_node_shop=null;
                redraw();
            }
        }

        if(mode==MODE.UNMAP_SHOP){
            Store temp = null;
            for(Location i : path_nodes){
                for(Store j : i.getStores()){
                    double distance_node_current = Math.pow(Math.pow(i.X-mouseEvent.getX(),2)+Math.pow(i.Y-mouseEvent.getY(),2),0.5);
                    double distance_store_current = Math.pow(Math.pow(j.X-mouseEvent.getX(),2)+Math.pow(j.Y-mouseEvent.getY(),2),0.5);
                    double distance_between = Math.pow(Math.pow(j.X-i.X,2)+Math.pow(j.Y-i.Y,2),0.5);
                    if(distance_node_current+distance_store_current-distance_between<1)
                        temp=j;
                }
                if(temp!=null)
                    i.getStores().remove(temp);
                temp=null;
                redraw();
            }
        }
    }

    private boolean pair_exists(Pair p) {
        for(Pair i : roads){
            if((i.A.equals(p.A)&&i.B.equals(p.B))||(i.A.equals(p.B)&&i.B.equals(p.A)))
                return true;
        }
        return false;
    }

    public void mouse_released(MouseEvent mouseEvent) {
        curr_selected_node=null; curr_selected_store=null;
    }

    public void map_dragged(MouseEvent mouseEvent) {
        if(mode==MODE.DISPLACE_NODE){
            if(curr_selected_node==null)
                curr_selected_node = get_Node(mouseEvent.getX(),mouseEvent.getY());
            else{
                for(Location i : path_nodes){
                    if(Math.abs(curr_selected_node.X-i.X)<20&&(curr_selected_node.Y-i.Y)<20&&!i.equals(curr_selected_node)&&Math.abs(i.X-mouseEvent.getX())<20&&Math.abs(i.Y-mouseEvent.getY())<20)return;
                }
                curr_selected_node.X=mouseEvent.getX();
                curr_selected_node.Y=mouseEvent.getY();
                redraw();
            }
        }

        if(mode==MODE.DISPLACE_SHOP){
            if(curr_selected_store==null)
                curr_selected_store = get_Store(mouseEvent.getX(),mouseEvent.getY());
            else{
                for(Store i : stores){
                    if(Math.abs(curr_selected_store.X-i.X)<20&&(curr_selected_store.Y-i.Y)<20&&!i.equals(curr_selected_store)&&Math.abs(i.X-mouseEvent.getX())<20&&Math.abs(i.Y-mouseEvent.getY())<20)return;
                }
                curr_selected_store.X=mouseEvent.getX();
                curr_selected_store.Y=mouseEvent.getY();
                redraw();
            }
        }
    }

    private Location get_Node(double x, double y) {
        for(Location i : path_nodes){
            if(i.match(x,y))
                return i;
        }
        return null;
    }

    private Store get_Store(double x, double y){
        for(Store i : stores){
            if(i.match(x,y))
                return i;
        }
        return null;
    }

    private Pair getPair(double x, double y){
        Pair returningpair=null;
        double accuracy=5;
        for(Pair i : roads){
            if(i.match(x,y)==null)continue;
            if(i.get_difference(x,y)<accuracy){returningpair=i;accuracy=i.get_difference(x,y);}
        }
        return returningpair;
    }

    public void set_clicked(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter efil = new FileChooser.ExtensionFilter("Images","*.jpg","*.jpeg","*.png",".bmp");
        fileChooser.getExtensionFilters().add(efil);
        File f = fileChooser.showOpenDialog(curr_stg);
        BufferedImage i = ImageIO.read(f);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(i,"png",bos);
        current_floor.background_map = bos.toByteArray();
        currentBackground = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(current_floor.background_map)) ,null);
        current_floor.CanvasWidth=currentBackground.getWidth();
        current_floor.CanvasHeight=currentBackground.getHeight();
        redraw();
    }

    public void remove_clicked(MouseEvent mouseEvent) {
        current_floor.background_map=null;
        currentBackground=null;
        redraw();
    }

    public void redraw(){
        painter.clearRect(0,0,map.getWidth(),map.getWidth());
        if(current_floor==null)return;

        map.setWidth(current_floor.CanvasWidth);
        map.setHeight(current_floor.CanvasHeight);

        painter.drawImage(currentBackground,0,0);

        for(Location i : path_nodes) {
            for(Store t : i.getStores()) {
                painter.setLineWidth(1);
                painter.setStroke(Color.valueOf(t.color));
                painter.strokeLine(i.X,i.Y,t.X,t.Y);
            }

            painter.setFill(Color.BLACK);
            if(p!=null)
                if(i.equals(p.A))painter.setFill(Color.PURPLE);
            painter.fillOval(i.X - 10, i.Y - 10, 20, 20);



        }
        painter.setLineWidth(3);
        for(Pair i : roads){
            painter.setStroke(Color.BLACK);
            painter.strokeLine(i.A.X,i.A.Y,i.B.X,i.B.Y);
        }
        painter.setLineWidth(5);
        for(Store i : stores){
            painter.setFill(Color.valueOf(i.color));
            painter.setStroke(Color.valueOf(i.color));
            painter.fillOval(i.X-15,i.Y-15,30,30);
        }
    }
}
