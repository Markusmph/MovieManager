import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.io.File;
import java.io.IOException;

import java.io.FileNotFoundException;

public class MovieManager extends Application{
    private TextField txtDirector, txtTitle, txtDuration, txtYear, txtClassification;
	private ObservableList<Movie> data;
    private ListView<Movie> lvMovie;
    private int totalMovies;
    
    public void start(Stage stage) throws Exception{
		BorderPane mainPane = new BorderPane();
		FlowPane contentPane = new FlowPane();
		FlowPane controlsPane = new FlowPane();
		mainPane.setCenter(contentPane);
		mainPane.setBottom(controlsPane);
		GridPane moviePane = new GridPane();
        
        Label lblTitle = new Label("Title");
		txtTitle = new TextField();
		moviePane.add(lblTitle, 0,0);
		moviePane.add(txtTitle, 1,0);
		
		Label lblDirector = new Label("Director");
		txtDirector = new TextField();
		moviePane.add(lblDirector, 0,1);
		moviePane.add(txtDirector, 1,1);
		
		Label lblDuration = new Label("Duration");
		txtDuration = new TextField();
		moviePane.add(lblDuration, 0,2);
		moviePane.add(txtDuration, 1,2);
		
		Label lblYear = new Label("Year");
		txtYear = new TextField();
		moviePane.add(lblYear, 0,3);
		moviePane.add(txtYear, 1,3);
		
		Label lblClassification = new Label("Classification");
		txtClassification = new TextField();
		moviePane.add(lblClassification, 0,4);
        moviePane.add(txtClassification, 1,4);
		
		contentPane.getChildren().add(moviePane);
		
		data = FXCollections.observableArrayList();
        lvMovie = new ListView<>(data);
        
        try{
            Movie[] movies = readMovies();
            for (int i = 0; i<movies.length; i++){
                data.add(movies[i]);
                totalMovies++;
            }
        } catch(FileNotFoundException f){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File not found");
            alert.setHeaderText("Error while reading the file");
            alert.setContentText(f.getMessage());
            alert.showAndWait();
        }

        lvMovie.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
                displayMovie();
			}
        });
        contentPane.getChildren().add(lvMovie);

		Button bttnAdd = new Button("Add");
        controlsPane.getChildren().add(bttnAdd);
        bttnAdd.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            public void handle(MouseEvent e) {
                addMovie();
            }
        });

        Button bttnDelete = new Button("Delete");
        controlsPane.getChildren().add(bttnDelete);
        bttnDelete.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            public void handle(MouseEvent e){
                deleteMovie();
            }
        });

        Button bttnSave = new Button("Save");
        controlsPane.getChildren().add(bttnSave);
        bttnSave.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                saveMovie();
            }
        });

        Button saveInFile = new Button("Save in file");
        controlsPane.getChildren().add(saveInFile);
        saveInFile.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            public void handle(MouseEvent e) {
                writeMovies();
            }
        });
        
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.show();
    }

    private Movie[] readMovies() throws Exception{
        FileInputStream fis = new FileInputStream("Movies.oop");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Movie[] movies = (Movie[]) ois.readObject();
        ois.close();
        return movies;
    }

    private void writeMovies(){
        try{
            Movie[] m = new Movie[totalMovies];
            for(int i = 0; i<m.length; i++)
                m[i] = data.get(i);
            FileOutputStream fos = new FileOutputStream("Movies.oop");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(m);
            oos.close();
        } catch(FileNotFoundException fe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File not found");
            alert.setHeaderText("Error while reading the file");
            alert.setContentText(fe.getMessage());
            alert.showAndWait();
        } catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    private void displayMovie(){
        try{
            Movie m = lvMovie.getSelectionModel().getSelectedItem();
            txtTitle.setText(m.getTitle());
            txtDirector.setText(m.getDirector());
            txtDuration.setText(m.getDuration());
            txtYear.setText(m.getYear());
            txtClassification.setText(m.getClassification());
        } catch(NullPointerException p){}
    }

    private void addMovie(){
        try{
            Movie m = new Movie();
            getMovie(m);
            data.add(m);
            setMovie(new Movie());
            totalMovies++;
        } catch(EmptyFieldException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add a movie");
            alert.setHeaderText("Error while adding a new movie");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void getMovie(Movie m) throws EmptyFieldException{
        m.setTitle(txtTitle.getText());
        m.setDirector(txtDirector.getText());
        m.setDuration(txtDuration.getText());
        m.setClassification(txtClassification.getText());
        m.setYear(txtYear.getText());
    }

    private void setMovie(Movie m){
        txtTitle.setText(m.getTitle());
        txtDirector.setText(m.getDirector());
        txtDuration.setText(m.getDuration());
        txtClassification.setText(m.getClassification());
        txtYear.setText(m.getYear());
    }

    private void deleteMovie(){
        Movie m = lvMovie.getSelectionModel().getSelectedItem();
        data.remove(m);
        setMovie(new Movie());
        totalMovies--;
    }

    private void saveMovie(){
        try {
            Movie m = lvMovie.getSelectionModel().getSelectedItem();
            int index = lvMovie.getItems().indexOf(m);
            getMovie(m);
            lvMovie.getItems().set(index, m);
            setMovie(new Movie());
        } catch(EmptyFieldException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Update a movie");
			alert.setHeaderText("Error while updating a movie");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}