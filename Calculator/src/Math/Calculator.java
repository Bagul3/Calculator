package Math;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
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
	  { "7", "8", "9", "/" },
      { "4", "5", "6", "*" },
      { "1", "2", "3", "-" },
      { "0", "c", "=", "+" }
  };

  private final Map<String, Button> accelerators = new HashMap<>();
  private BasicOperations basicOperations = new BasicOperations();
  private FloatProperty stackValue = new SimpleFloatProperty();
  private FloatProperty value = new SimpleFloatProperty();
  private FloatProperty memoryValue = new SimpleFloatProperty();
  
  private enum Op { NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE }
  private Op curOp   = Op.NOOP;
  private Op stackOp = Op.NOOP;

  public static void main(String[] args) { launch(args); }

  @Override public void start(Stage stage) {
    final TextField screen  = createScreen();
    final TilePane  buttons = createButtons();

    stage.setTitle("Fred Blogg’s Calculator");
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setScene(new Scene(createLayout(screen, buttons)));
    stage.show();
  }

  private VBox createLayout(TextField screen, TilePane buttons) {
    final VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: grey; -fx-padding: 20; -fx-font-size: 20;");
    layout.getChildren().setAll(screen, buttons);
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
    screen.textProperty().bind(Bindings.format("%.0f", value));
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

    if (buttonIcon.matches("[0-9]")) 
      numericButton(buttonIcon, button);
    else {
      final ObjectProperty<Op> operation = setOperator(buttonIcon);
      if (operation.get() != Op.NOOP) 
    	  operationButton(button, operation);
      else if ("c".equals(buttonIcon)) 
        clearButton(button);
      else if ("=".equals(buttonIcon)) 
        equalsButton(button);
      else if ("m".equals(buttonIcon)) 
    	  msetButton(button);
      else if ("m+".equals(buttonIcon))
    	  mplusButton(button);
      else if ("m-".equals(buttonIcon))
    	  mminiusButton(button);
      else if ("mc".equals(buttonIcon))
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

  private void numericButton(final String buttonValue, Button button) {
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if (curOp == Op.NOOP) 
        	if(String.valueOf(value.get()).length() != 7)
        		value.set((float) Math.floor(value.get() * 10 + Integer.parseInt(buttonValue)));         
        else {
          stackValue.set(value.get());
          value.set(Integer.parseInt(buttonValue));
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
        value.set(0);
      }
    });
  }

  private void equalsButton(Button button) {
    button.setStyle("-fx-base: ghostwhite;");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        switch (stackOp) {
          case ADD:      value.set(basicOperations.addFunction(stackValue.get(),value.get())); break;
          case SUBTRACT: value.set(basicOperations.subtractionFunction(stackValue.get(),value.get())); break;
          case MULTIPLY: value.set(basicOperations.mutlplicationFunction(stackValue.get(),value.get())); break;
          case DIVIDE:   value.set(basicOperations.divisionFunction(stackValue.get(),value.get())); break;
        }
      }
    });
  }
  
  private void mplusButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(basicOperations.mplusFunction(value.get()));
	      }
	    });
  }
  
  private void mminiusButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(basicOperations.mminiusFunction(value.get()));
	      }
	    });
  }
  
  private void mclearButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(basicOperations.mclearFunction());
	      }
	    });
  }
  
  private void msetButton(Button button){
	  button.setStyle("-fx-base: ghostwhite;");
	    button.setOnAction(new EventHandler<ActionEvent>() {
	      @Override
	      public void handle(ActionEvent actionEvent) {
	          memoryValue.set(basicOperations.msetFunction(value.get()));
	      }
	    });
  }
  
}