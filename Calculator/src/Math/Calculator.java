package Math;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;

// a simple JavaFX calculator.
public class Calculator extends Application {
  private static final String[][] template = {
	  { "M+", "M-", "M", "MC"},
	  {"","","","C"},
	  { "7", "8", "9", "/" },
      { "4", "5", "6", "*" },
      { "1", "2", "3", "-" },
      { "0", ".", "=", "+" }
  };

  private final Map<String, Button> accelerators = new HashMap<>();
  private BasicOperations basicOperations = new BasicOperations();
  private FloatProperty stackValue = new SimpleFloatProperty();
  private StringProperty value = new SimpleStringProperty();
  private StringProperty memoryValue = new SimpleStringProperty();
  
  private enum Op { NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE }
  private enum Opm { M, MC, MPlus, MMinus }
  private Op curOp   = Op.NOOP;
  private Op stackOp = Op.NOOP;
  
  private boolean clearAfterEquals = false;

  public static void main(String[] args) { launch(args); }

  @Override public void start(Stage stage) {
	value.set("");
	memoryValue.set("");
    final TextField screen  = createScreen();
    final TextField memoryScreen = createMemoryScreen();
    final TilePane  buttons = createButtons();
    
    stage.setTitle("Fred Blogg’s Calculator");
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setScene(new Scene(createLayout(screen, memoryScreen, buttons)));
    stage.show();
  }

  private VBox createLayout(TextField screen, TextField memoryScreen, TilePane buttons) {
    final VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: grey; -fx-padding: 20; -fx-font-size: 20;");
    layout.getChildren().setAll(screen, memoryScreen, buttons);
    handleAccelerators(layout);
    screen.prefWidthProperty().bind(buttons.widthProperty());
    return layout;
  }

  private void handleAccelerators(VBox layout) {	 
    layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        Button activated = accelerators.get(keyEvent.getText());
        if (activated != null) 
          activated.fire();
      }
    });
  }

  private TextField createScreen() {
    final TextField screen = new TextField();
    screen.setStyle("-fx-background-color: aquamarine;");
    screen.setAlignment(Pos.CENTER_RIGHT);
    screen.setEditable(false);
    screen.textProperty().bind(value);
    return screen;
  }
  
  private TextField createMemoryScreen() {
	    final TextField screen = new TextField();
	    screen.setStyle("-fx-background-color: pink;");
	    screen.setAlignment(Pos.BASELINE_LEFT);
	    screen.setEditable(false);
	    screen.textProperty().bind(memoryValue);
	    return screen;
	  }

  private TilePane createButtons() {
    TilePane buttons = new TilePane();
    buttons.setVgap(7);
    buttons.setHgap(7);
    buttons.setPrefColumns(template[0].length);
    for (String[] row: template) {
      for (String buttonIcon: row) 
        buttons.getChildren().add(createButton(buttonIcon));      
    }
    return buttons;
  }

  private Button createButton(final String buttonIcon) {
    Button button = standardButton(buttonIcon);

    if (buttonIcon.matches("[0-9]")||buttonIcon.equals(".")) 
      numericButton(buttonIcon, button);
    else {
      final ObjectProperty<Op> operation = setOperator(buttonIcon);
      if (operation.get() != Op.NOOP) 
    	  operationButton(button, operation);
      else if ("C".equals(buttonIcon)) 
        clearButton(button);
      else if ("=".equals(buttonIcon)) 
        equalsButton(button);
      else if ("M".equals(buttonIcon)) 
    	  msetButton(button);
      else if ("M+".equals(buttonIcon))
    	  mplusButton(button);
      else if ("M-".equals(buttonIcon))
    	  mminiusButton(button);
      else if ("MC".equals(buttonIcon))
    	  mclearButton(button);
    }

    return button;
  }

  private ObjectProperty<Op> setOperator(String operation) {
    final ObjectProperty<Op> operationTrigger = new SimpleObjectProperty<>(Op.NOOP);
    switch (operation) {
      case "+": operationTrigger.set(Op.ADD);
      	break;
      case "-": operationTrigger.set(Op.SUBTRACT); 
      	break;
      case "*": operationTrigger.set(Op.MULTIPLY); 
      	break;
      case "/": operationTrigger.set(Op.DIVIDE);   
      	break;
    }
    return operationTrigger;
  }

  private void operationButton(Button button, final ObjectProperty<Op> triggerOp) {
    button.setStyle("-fx-base: lightgray;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {    	
    	clearAfterEquals = false;
        curOp = triggerOp.get();
      }
    });
  }

  private Button standardButton(String s) {
    Button button = new Button(s);
    button.setStyle("-fx-base: beige;");
    accelerators.put(s, button);
    button.setMaxSize(Float.MAX_VALUE, Float.MAX_VALUE);
    return button;
  }

  private void numericButton(final String numericValue, Button button) {
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {   	    	
	    	if(clearAfterEquals)  {
	    		value.set("");
	    		clearAfterEquals = false;
	    	}
	        if (curOp == Op.NOOP) {        		
	        	value.set(value.get() + numericValue);     		
	        		
	        } else {
	          stackValue.set(Float.valueOf(value.get()));
	          value.set(numericValue);
	          stackOp = curOp;
	          curOp = Op.NOOP;
	        }
	      }
	    });
	  }

  private void clearButton(Button button) {
    button.setStyle("-fx-base: mistyrose;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        value.set("");
      }
    });
  }

  private void equalsButton(Button button) {	
    button.setStyle("-fx-base: ghostwhite;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @SuppressWarnings("incomplete-switch")
      public void handle(ActionEvent actionEvent) {
    	clearAfterEquals = true;
        switch (stackOp) {
          case ADD:      value.set(basicOperations.addFunction(stackValue.get(),value.get())); break;
          case SUBTRACT: value.set(basicOperations.subtractionFunction(stackValue.get(),value.get())); break;
          case MULTIPLY: value.set(basicOperations.mutlplicationFunction(stackValue.get(),value.get())); break;
          case DIVIDE:  
        	  if(value.get().substring(0, 1).equals("0")) {
        		  value.set("Error! Can not divide by 0!");
        		  break;
        	  }
        	  value.set(basicOperations.divisionFunction(stackValue.get(),value.get())); break;
        }
      }
    });
  }
  
  private void mplusButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(String.valueOf(basicOperations.mplusFunction(Float.valueOf(value.get()))));
	      }
	    });
  }
  
  private void mminiusButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(String.valueOf(basicOperations.mminiusFunction(Float.valueOf(value.get()))));
	      }
	    });
  }
  
  private void mclearButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(String.valueOf(basicOperations.mclearFunction()));
	      }
	    });
  }
  
  private void msetButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(String.valueOf(basicOperations.msetFunction(Float.valueOf(value.get()))));
	      }
	    });
  }
  
}