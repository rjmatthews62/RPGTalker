package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Controller implements LineListener {

    Stage stage=null;
    @FXML

    ListView<Mixer.Info> fruitCombo;

    @FXML
    ListView<File> soundfile;

    @FXML
    TextArea status;

    ObservableList<Mixer.Info> list = FXCollections.observableArrayList();
    ObservableList<File> soundlist = FXCollections.observableArrayList();

    ArrayList<Clip> clips = new ArrayList<Clip>();

    @FXML
    public void onPopulate(ActionEvent action) {
        list.clear();
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        for (Mixer.Info i : info) {
            list.add(i);
        }
        fruitCombo.setItems(list);
        File f = new File("Sounds");
        File[] flist = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }
        });
        soundlist.setAll(flist);
        soundfile.setItems(soundlist);
        if (stage!=null) stage.sizeToScene();
    }

    @FXML
    public void onPlay(ActionEvent actionEvent) {
        Mixer.Info info = fruitCombo.getSelectionModel().getSelectedItem();
        File f = soundfile.getSelectionModel().getSelectedItem();
        if (info==null) {
            addln("No device selected.");
            return;
        }
        if (f==null) {
            addln("No file selected.");
            return;
        }

        try {
            addln("Play "+f+" to "+info);
            Clip clip = AudioSystem.getClip(info);
            AudioInputStream sound = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
            clip.open(sound);
            clip.start();
            if (!clips.contains(clip)) {
                clips.add(clip);
                clip.addLineListener(this);
            }
        } catch (LineUnavailableException e) {
            addln(e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            addln(e.getMessage());
        } catch (IOException e) {
            addln(e.getMessage());
        }
    }

    public void addln(Object msg) {
        status.appendText(msg.toString()+"\n");
    }

    @Override
    public void update(LineEvent event) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                addln(event);
            }
        });
    }

    public void onStop(ActionEvent actionEvent) {
        addln("Stopping.");
        for (Clip c : clips) c.close();
        clips.clear();
    }
}
