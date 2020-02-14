package com.arm.test;

import com.navin.Store;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddStore {
    Store s;
    Stage curr_stg;

    @FXML TextField box;
    @FXML Button add;
    @FXML ColorPicker cp;

    public void add_clicked(MouseEvent mouseEvent) {
        s.ID=box.getText();
        s.color= cp.getValue().toString();
        curr_stg.close();
    }

    public void setStore_and_get_stage(Store temp, Stage s) {
        this.s=temp;
        this.curr_stg=s;
        box.setText(temp.ID);
        cp.setValue(Color.valueOf(temp.color));
    }

    public void cancel(MouseEvent mouseEvent) throws Throwable {
        if(curr_stg.getTitle().equals("Add Store")){s.X=-1;System.out.println("CANCEL");}
        curr_stg.close();
    }
}
