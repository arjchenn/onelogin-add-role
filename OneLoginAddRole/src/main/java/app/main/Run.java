package app.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Run extends Application {
	
	private Label statusLabel = new Label("");
	private ArrayList<String> names;
	private Main main;
	private ArrayList<String> succeeded;
	private ArrayList<String> fail;
	private Stage statusStage;

	public static void main(String[] args) throws Exception {
		
		launch(args);

	}


	public void start(Stage primaryStage) throws Exception {
		
		Label idLabel = new Label("Client ID:");
		Label secretLabel = new Label("Client Secret:");
		final TextField idField = new TextField();
		final TextField secretField = new TextField();
		Button btn = new Button();

	    btn.setText("Add Users to Role");
	    
	    btn.setOnAction(new EventHandler<ActionEvent>() {

	        public void handle(ActionEvent event) {
	            try {
					addToRole(secretField.getText(), idField.getText());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	        }
	    });

	    StackPane root = new StackPane();
	    root.getChildren().add(btn);
	    secretField.setMinWidth(200);
	    root.getChildren().add(secretField);
	    StackPane.setMargin(secretField, new Insets(0,28,160,100));
	    root.getChildren().add(idField);
	    StackPane.setMargin(idField, new Insets(0,28,100,100));
	    root.getChildren().add(secretLabel);
	    StackPane.setMargin(secretLabel, new Insets(0,435,160,0));
	    root.getChildren().add(idLabel);
	    StackPane.setMargin(idLabel, new Insets(0,414,100,0));
	    root.getChildren().add(statusLabel);
	    StackPane.setMargin(statusLabel, new Insets(500,0,0,0));

	    Scene scene = new Scene(root, 550, 200);

	    primaryStage.setTitle("OneLogin");
	    primaryStage.setScene(scene);
	    primaryStage.setResizable(false);
	    primaryStage.show();
	    
	}

	@SuppressWarnings("deprecation")
	public void addToRole(String secretIn, String idIn) throws IOException, URISyntaxException{
		main = new Main();
		Integer roleId = null;

		if (!secretIn.replaceAll("\\b[a-fA-F0-9]{64}\\b", "").equals("") || secretIn.equals("")){
			Alert alert1 = new Alert(AlertType.WARNING);
			alert1.setTitle("Invalid Secret");
			alert1.setHeaderText("Invalid Secret");
			alert1.setContentText("Invalid Secret");

			alert1.showAndWait();
			return;
			
		}
		
		if (!idIn.replaceAll("\\b[a-fA-F0-9]{64}\\b", "").equals("") || idIn.equals("")){
			Alert alert1 = new Alert(AlertType.WARNING);
			alert1.setTitle("Invalid ID");
			alert1.setHeaderText("Invalid ID");
			alert1.setContentText("Invalid ID");

			alert1.showAndWait();
			return;
		}

		names = new ArrayList<String>();
		succeeded = new ArrayList<String>();
		fail = new ArrayList<String>();

		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Enter Role ID");
		dialog.setHeaderText("");
		dialog.setContentText("Please enter valid role ID:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) roleId = Integer.parseInt(result.get());

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File file = fileChooser.showOpenDialog(new Stage());

		FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        while (iterator.hasNext()) {
        	String nextString = "";
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        nextString = nextString.concat(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        nextString = nextString.concat(Boolean.toString(cell.getBooleanCellValue()));
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        nextString = nextString.concat(Double.toString(cell.getNumericCellValue()));
                        break;
                }
            }
            names.add(nextString);
        }

        for (String x : names) System.out.println(x);
        workbook.close();
        inputStream.close();

        if(main.getToken(idIn, secretIn)){

	        final String role = main.getRole(roleId);
	        StackPane status = new StackPane();
	        Label label = new Label("");
		    status.getChildren().add(label);

		    Scene scene2 = new Scene(status, 500, 75);

		    statusStage = new Stage();
		    statusStage.setTitle("Status: Adding to role " + role);
		    statusStage.setScene(scene2);
		    statusStage.show();
		    statusStage.toFront();

	        final int id = roleId;

	        Task<Void> task = new Task<Void>() {
	            @Override public Void call() {
		        	try {
						for(String x : names){
							updateMessage("Adding user " + x + " to role " + role);
							if(main.addRole(id, main.getId(x)) == false) {
								succeeded.add(x);

							}else{
								fail.add(x);
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
	            }
	        };

	        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	            public void handle(WorkerStateEvent t) {
	            	String showSucceeded = Integer.toString(succeeded.size()) + " Succeeded:\n";
			        String showFailed = Integer.toString(fail.size()) + " Failed:\n";

			        for(String x : succeeded) showSucceeded = showSucceeded.concat("\t" + x + "\n");
			        for(String x : fail) showFailed = showFailed.concat("\t" + x + "\n");
			        showResults(showSucceeded, showFailed);
			        statusStage.hide();

	            }
	        });

	        label.textProperty().bind(task.messageProperty());
	       new Thread(task).start();
        }

	}

	private void showResults(String succeeded, String failed){
		StackPane root = new StackPane();
        TextArea area = new TextArea();
        area.appendText(succeeded + failed);
	    root.getChildren().add(area);

	    Scene scene = new Scene(root, 300, 500);

	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Results");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}


