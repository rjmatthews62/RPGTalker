package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Controller implements LineListener {

    public CheckBox filter;
    Stage stage = null;
    @FXML

    ListView<Mixer.Info> fruitCombo;

    @FXML
    ListView<File> soundfile;

    @FXML
    public ListView playlist;

    @FXML
    TextArea status;

    ObservableList<Mixer.Info> list = FXCollections.observableArrayList();
    ObservableList<File> soundlist = FXCollections.observableArrayList();
    ObservableList<ClipHolder> clips = FXCollections.observableArrayList();

    @FXML
    public void onPopulate(ActionEvent action) {
        boolean filtered=filter.isSelected();
        list.clear();
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        for (Mixer.Info i : info) {
            if (filtered) {
                try {
                    Clip clip=AudioSystem.getClip(i);
                } catch (LineUnavailableException e) {
                    continue;
                }
            }

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
        playlist.setItems(clips);
        if (stage != null) stage.sizeToScene();
    }

    @FXML
    public void onPlay(ActionEvent actionEvent) {
        Mixer.Info info = fruitCombo.getSelectionModel().getSelectedItem();
        File f = soundfile.getSelectionModel().getSelectedItem();
        if (info == null) {
            addln("No device selected.");
            return;
        }
        if (f == null) {
            addln("No file selected.");
            return;
        }

        try {
            addln("Play " + f + " to " + info);
            Clip clip = AudioSystem.getClip(info);
            AudioInputStream sound = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
            addln("Sound="+sound.getFormat());
            addln( "Line="+clip.getLineInfo());
            clip.open(sound);
            clip.start();
            if (!hasClip(clip)) {
                clips.add(new ClipHolder(f.toString(),clip));
                clip.addLineListener(this);
            }
        } catch (LineUnavailableException e) {
            addln(e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            addln(e.getMessage());
        } catch (IOException e) {
            addln(e.getMessage());
        } catch (Exception e) {
            addln(e.getMessage());
        }
    }

    private boolean hasClip(Clip clip) {
        for (ClipHolder c : clips) {
            if (c.clip.equals(clip)) {
                return true;
            }
        }
        return false;
    }

    public void addln(Object msg) {
        if (Platform.isFxApplicationThread()) {
            status.appendText(msg.toString() + "\n");
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    addln(msg);
                }
            });
        }
    }

    @Override
    public void update(LineEvent event) {
        addln(event);
        if (event.getType()==LineEvent.Type.STOP) {
            ((Clip) event.getSource()).close();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ClipHolder c : clips) {
                        if (!c.clip.isOpen()) {
                            clips.remove(c);
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    // Ignore.
                }
                playlist.setItems(null);
                playlist.setItems(clips);
            }
        });
    }

    public void onStop(ActionEvent actionEvent) {
        addln("Stopping.");
        for (ClipHolder c : clips){
            c.clip.close();
        }
        clips.clear();
    }

    public void onFilter(ActionEvent actionEvent) {
        addln("Filtering.");
        onPopulate(actionEvent);
    }

    class ClipHolder {
        Clip clip;
        String caption;

        ClipHolder(String acaption, Clip c) {
            clip=c;
            caption=acaption;        }

        @Override
        public String toString() {
            String result=caption+" ";
            if (clip.isRunning()) result+="running";
            else if (clip.isActive()) result+="active";
            else if (clip.isOpen()) result+="open";
            else result+="closed";
            return result;
        }

    }
}
