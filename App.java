//program created by elijah gonzalez, finished on october 14th at 8:41pm. all that was left after that was comments, but that doesnt change the functionality of the code so 
//i stopped it there. i will explain what everything does in the comments. oringally, this project was going to have a lot more, but as i procrastinated more and more, i realized
//as i was working on it over the past week or so, i had bit off way more than i could chew. so, what this project is a simple battle system. or so i had thought. it turned out
//that so much went into the sytem, it had been around. 350 lines of code, not including this top paragraph. but despite it being less than what i had in mind, it works flawlessly.
//so, im happy about that. and now to explain what the program does: the player is fighting an enemy. the player can attack, defend, or wild attack. if the player attacks,
//the program takes the players attack stat - the enemys defense stat, which then = the total damage done to the player. if the player defends, they get a defense boost until its their
//next turn, then it goes back to its default value. if the player does a wild attack, they get an attack boost and attack on the same turn, however their defense is set to 0 until
//its the players turn again. as for the enemy, the same applies. except the enemy is a "bot", where it just chooses what it wants to do randomly. if the player loses, a window
//pops up telling them they lost, after clicking ok, the entire thing shuts down. and if the player wins, it congratulates them, then the window shuts down. i also added last minute
//that if the player wins, they can input their score, being how many turns it took, the lower the better. i also added a menu at the start that asks if they want to look at scores,
//or if they want to just to fight the enemy. the scores are put into and read from a text file called results.

import javafx.animation.PauseTransition; //import for pause transitions in animations
import javafx.application.Application; //base class for javafx
import javafx.scene.Scene; //represents the scene 
import javafx.scene.control.Button; //button components
import javafx.scene.control.Label; //label component for displaying text
import javafx.scene.control.Alert; //alert dialog box
import javafx.scene.control.Alert.AlertType; //enum for alert types
import javafx.scene.control.TextInputDialog; //dialog for text inputs
import javafx.scene.image.Image; //class for images
import javafx.scene.image.ImageView; //image view class to display images
import javafx.scene.layout.HBox; //layout for arranging nodes horizontally
import javafx.scene.layout.VBox; //layout for arranging nodes vertically
import javafx.stage.Stage; //stage is a top-level JavaFX container
import javafx.util.Duration; //for durations in animations
import java.util.Random; //for generating random numbers
import java.io.BufferedReader; //for reading from a file
import java.io.BufferedWriter; //for writing to a file
import java.io.FileReader; //for file operations
import java.io.FileWriter; //for file operations
import java.io.IOException; //for handling exceptions

//abstract class for common character properties and methods
abstract class Character {

    //health points of the character
    protected int hp;  

    //same with atk
    protected int atk; 

    //and def
    protected int def; 

    //indicates if the character is defending
    protected boolean isDefending; 

    //indicates if the wild attack has been used by the character
    protected boolean wildAttackUsed; 

    //constructor to initialize character properties
    public Character(int hp, int atk, int def) {

        //initialize health points (hp)
        this.hp = hp; 

        //same with attack(atk)
        this.atk = atk; 

        //and def (dp)
        this.def = def; 

        //default defending state is false
        this.isDefending = false; 

        //default wild attack state is false
        this.wildAttackUsed = false; 
    }

    //getter method to retrieve health points
    public int getHP() { return hp; }

    //setter method to set health points
    public void setHP(int hp) { this.hp = hp; }

    //same with attack here, getting it
    public int getATK() { return atk; }

    //method to compute defense based on whether the character is defending
    public int getDEF() {

        //increase defense by 1 if defending
        return isDefending ? def + 1 : def; 
    }

    //method to check if the character is alive (has health points greater than 0)
    public boolean isAlive() { return hp > 0; }

    //method to enable the defending state
    public void defend() { this.isDefending = true; }

    //method to reset the defending state to false
    public void resetDefense() {
        this.isDefending = false; 
    }

    //method to mark that the wild attack has been used
    public void useWildAttack() {

        //set wildAttackUsed to true
        wildAttackUsed = true; 

        //reset defending state when wild attack is used
        this.isDefending = false; 
    }

    //getter method to check if the wild attack has been used
    public boolean isWildAttackUsed() {

        //return the status of wild attack usage
        return wildAttackUsed; 
    }

    //method to reset the wild attack state to unused
    public void resetWildAttack() {

        //set wildAttackUsed back to false 
        wildAttackUsed = false; 
    }
}

//class for player characters extending the Character class
class PlayerInfo extends Character {

    //constructor for player character; takes health, attack, and defense values
    public PlayerInfo(int hp, int atk, int def) { 

        //call the constructor of the parent class (Character)
        super(hp, atk, def); 
    }

    //method for performing a regular attack on the enemy
    public int performAttack(EnemyInfo enemy) {

        //set defense to 2 during attack, as thats the normal value
        this.def = 2; 

        //calculate damage dealt
        int damageDealt = Math.max(0, this.atk - enemy.getDEF());

        //reduce enemy's health
        enemy.setHP(Math.max(0, enemy.getHP() - damageDealt));

        //return the amount of damage dealt
        return damageDealt; 
    }

    //method for performing a wild attack on the enemy
    public int performWildAttack(EnemyInfo enemy) {

        //set defense to 0 during wild attack
        this.def = 0; 

        //mark wild attack as used
        useWildAttack(); 

         //ensure defending state is false
        this.isDefending = false;
        
        //calculate damage dealt
        int damageDealt = Math.max(0, (this.atk + 2) - enemy.getDEF()); 

        //reduce enemy's health
        enemy.setHP(Math.max(0, enemy.getHP() - damageDealt)); 

        //return the amount of damage dealt
        return damageDealt; 
    }

    //method to reset the player's defense state and return it to initial value
    public void resetDefense() {
        this.isDefending = false; 

        //reset defense to original value
        this.def = 2; 
    }
}

//class for enemy characters extending the character class
class EnemyInfo extends Character {

    //constructor for enemy character. takes health, attack, and defense values
    public EnemyInfo(int hp, int atk, int def) { 

        //call the constructor of the parent class (Character)
        super(hp, atk, def); 
    }

    //override defense calculation for the enemy
    @Override
    public int getDEF() {

        //add defense if defending
        return isDefending ? def + 1 : def; 
    }

    //method for enemy to perform an attack on the player
    public int performAttack(PlayerInfo player) {

        //calculate damage dealt to the player
        int damageDealt = Math.max(0, this.atk - player.getDEF()); 

        //reduce player's health
        player.setHP(Math.max(0, player.getHP() - damageDealt)); 

         //return the amount of damage dealt
        return damageDealt;
    }

    //method for enemy to perform a wild attack on the player
    public int performWildAttack(PlayerInfo player) {

        //mark wild attack as used
        useWildAttack(); 

        //store original attack value
        int originalAtk = this.atk; 

        //increase attack by 2 for wild attack
        this.atk += 2; 

        //calculate damage dealt to the player
        int damageDealt = Math.max(0, this.atk - player.getDEF());

        //reduce player's health
        player.setHP(Math.max(0, player.getHP() - damageDealt)); 

        //reset attack to its original value
        this.atk = originalAtk;

        //set defense to 0 during wild attack
        this.def = 0; 

        //return the amount of damage dealt
        return damageDealt; 
    }

    //method to enable defending state for the enemy
    public void defend() {

        //mark defending state as true
        this.isDefending = true; 

        //set defense to 2 when defending
        def = 2; 
    }

    // Method to reset the enemy's defense state after their turn
    public void resetDefense() {

        //reset defending state
        this.isDefending = false; 

         //reset defense back to 2 after attacking
        def = 2;
    }
}

//main application class
public class App extends Application {
    
    private PlayerInfo player; //instance variable for player character
    private EnemyInfo enemy; //same with enemy
    private Label playerStatsLabel, enemyStatsLabel, actionLogLabel; //labels for displaying stats and actions
    private Button attackButton, defendButton, wildAttackButton; //buttons for user interactions
    private ImageView enemyImageView; //imageView for displaying the enemy's image
    private Random random; //random number generator for actions
    private int turnCount; //counter for the number of turns taken
    private Stage primaryStage; //reference to the main stage for the application
    
    //main method to launch the application
    public static void main(String[] args) { 

        //launch the JavaFX application
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) {

        //store reference to the primary stage
        this.primaryStage = primaryStage; 

        //show the main menu
        showMainMenu(); 
    }
    
    //method to show the main menu
    private void showMainMenu() {

        //create a vertical box with spacing
        VBox menuLayout = new VBox(10); 

        //this is a title label
        Label titleLabel = new Label("Welcome to the Battle Game!"); 
        
        //button to start a fight
        Button fightButton = new Button("Fight the Enemy"); 

        //button to view scores
        Button scoresButton = new Button("View Scores"); 
        
        //set actions for buttons, if startGame is clicked, start game, if show scores is clicked, go to scores
        fightButton.setOnAction(e -> startGame()); 
        scoresButton.setOnAction(e -> showScores());
        
        //add components to menu layout
        menuLayout.getChildren().addAll(titleLabel, fightButton, scoresButton); 

        //set padding for the layout
        menuLayout.setPadding(new javafx.geometry.Insets(20)); 

        //center the layout
        menuLayout.setAlignment(javafx.geometry.Pos.CENTER); 

        //set the scene to the primary stage
        primaryStage.setScene(new Scene(menuLayout, 300, 200)); 

        //set title of the window
        primaryStage.setTitle("Main Menu"); 

         //show the window
        primaryStage.show();
    }

    //method to start the game
    private void startGame() {

        //initialize player with health, attack, and defense values
        player = new PlayerInfo(10, 3, 2); 

        //initialize enemy with health, attack, and defense values
        enemy = new EnemyInfo(8, 3, 2); 

        //initialize random number generator
        random = new Random(); 

        //initialize the turn counter
        turnCount = 0;
        
        //create labels to display player and enemy stats and actions, for action, enemy, and player
        playerStatsLabel = new Label(getPlayerStats()); 
        enemyStatsLabel = new Label(getEnemyStats()); 
        actionLogLabel = new Label("Actions:"); 

        //create buttons for attack actions, for attack, defend, and wild attack
        attackButton = new Button("Attack"); 
        defendButton = new Button("Defend"); 
        wildAttackButton = new Button("Wild Attack"); 

        // Add action handlers for the buttons, attack, defend, and wild attack
        attackButton.setOnAction(e -> performAttack()); 
        defendButton.setOnAction(e -> performDefend()); 
        wildAttackButton.setOnAction(e -> performWildAttack()); 

        //load and setup the enemy's image
        enemyImageView = new ImageView(new Image("file:///C:/Users/Elijah/javaPrograms/FinalGame/TheGame/src/CircleMan.png"));

        //set width for the enemy image
        enemyImageView.setFitWidth(100); 

        //preserve the aspect ratio of the enemy image
        enemyImageView.setPreserveRatio(true); 

        //create a horizontal box for arranging buttons with spacing
        HBox buttonBox = new HBox(10, attackButton, defendButton, wildAttackButton);

        //center the button box
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER); 

        //create the main layout vertically arranging all components
        VBox layout = new VBox(10, playerStatsLabel, enemyStatsLabel, enemyImageView, actionLogLabel, buttonBox);

        //center the layout
        layout.setAlignment(javafx.geometry.Pos.CENTER); 

        //add padding around layout
        layout.setPadding(new javafx.geometry.Insets(20));

        //set the scene and show the application window, set the layout, title of the window, and applicatoin window
        primaryStage.setScene(new Scene(layout, 400, 400)); 
        primaryStage.setTitle("Choose Your Attack"); 
        primaryStage.show(); 
    }

    //method to show scores
    private void showScores() {

        //create a vertical box for scores layout
        VBox scoresLayout = new VBox(10); 

        //label for scores head
        Label scoresLabel = new Label("Scores:"); 

        //initialize string builder for reading scores from the file
        StringBuilder scores = new StringBuilder();

        //use BufferedReader to read from a file
        try (BufferedReader reader = new BufferedReader(new FileReader("results.txt"))) { 

            //variable to store each line read
            String line; 

            //read each line until end of file
            while ((line = reader.readLine()) != null) { 

                //append each line to scores string
                scores.append(line).append("\n"); 
            }

        //handle exceptions while reading file
        } catch (IOException e) { 

            //message if there is an error
            scores.append("Error loading scores.");
        }

        //set contents to scores label
        Label scoresContent = new Label(scores.toString()); 

        //button to go back to main menu
        Button backButton = new Button("Back"); 
        
        //action to return to the main menu when back button is clicked
        backButton.setOnAction(e -> showMainMenu()); 
        
        //add components to scores layout, set padding, as well as centering it
        scoresLayout.getChildren().addAll(scoresLabel, scoresContent, backButton); 
        scoresLayout.setPadding(new javafx.geometry.Insets(20)); 
        scoresLayout.setAlignment(javafx.geometry.Pos.CENTER); 

        //set the scene to show scores, title, and scores window
        primaryStage.setScene(new Scene(scoresLayout, 400, 300)); 
        primaryStage.setTitle("Scores"); 
        primaryStage.show(); 
    }

    //method for performing a regular attack
    private void performAttack() {

        //check if the enemy is already defeated
        if (!enemy.isAlive()) { 

            //log message if enemy is defeated
            actionLogLabel.setText("The enemy is already defeated!"); 

            //exit after
            return;
        }

         //increase turn count
        turnCount++;

        //player attacks the enemy and gets damage dealt
        int damageDealt = player.performAttack(enemy); 

        //log action
        actionLogLabel.setText("You attacked the enemy for " + damageDealt + " damage! Enemy HP is now: " + enemy.getHP());

        //update the stat labels
        updateStats(); 

        //check if enemy has been defeated
        if (!enemy.isAlive()) {

            //log victory message
            actionLogLabel.setText("You defeated the enemy!");

            //show victory alert to the player
            showVictoryAlert(); 

            //hide enemy image
            enemyImageView.setVisible(false); 

            //exit the method
            return; 
        }

        //pause before executing enemy's turn, then call method to pause before enemy's turn
        pauseBeforeEnemyTurn(); 
    }

    //method for performing a defend action
    private void performDefend() {

        //check if the enemy is already defeated
        if (!enemy.isAlive()) { 

            //log message if enemy is defeated
            actionLogLabel.setText("The enemy is already defeated!"); 

            //exit the method
            return; 
        }

        //increment the turn counter
        turnCount++; 

         //player chooses to defend
        player.defend();

        //log defending action
        actionLogLabel.setText("You chose to defend!");
        
        //update the stat labels
        updateStats(); 

        //pause before executing enemy's turn
        pauseBeforeEnemyTurn(); // Call method to pause before enemy's turn
    }

    //method for performing a wild attack
    private void performWildAttack() {
    
        //check if the enemy is already defeated
        if (!enemy.isAlive()) {

            //log message if enemy is defeated
            actionLogLabel.setText("The enemy is already defeated!"); 

            //exit here...
            return; 
        }

        //increase!!!!!!
        turnCount++; 

        //player performs a wild attack
        int damageDealt = player.performWildAttack(enemy); 

        //log the action
        actionLogLabel.setText("You performed a wild attack for " + damageDealt + " damage! Enemy HP is now: " + enemy.getHP()); 

        //update the stat labels
        updateStats(); 

        //check if the enemy is defeated
        if (!enemy.isAlive()) { 

            //log victory message
            actionLogLabel.setText("You defeated the enemy!"); 

            //show victory alert to the player
            showVictoryAlert(); 

            //hide enemy to signal it's defeat
            enemyImageView.setVisible(false);

            //exit method
            return; 
        }

        //pause before executing the enemy's turn, then call method to pause before the enemy moves
        pauseBeforeEnemyTurn(); 
    }

    //pause before the enemy's turn
    private void pauseBeforeEnemyTurn() {
        //disable the buttons during the enemy's turn, which is the attack, defend, and wild attack
        attackButton.setDisable(true);
        defendButton.setDisable(true);
        wildAttackButton.setDisable(true); 
    
        //pause for one second so the player cannot spam, then set the action to execute after pause, then play the pause
        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Create a one-second pause
        pause.setOnFinished(event -> enemyTurn());
        pause.play(); 
    }
    
    //method for handling the enemy's turn
    private void enemyTurn() {

        //check if player is alive
        if (!player.isAlive()) { 

            //log defeated message
            actionLogLabel.setText("You have been defeated!"); 

            //show defeat alert
            showDefeatAlert();

            //exit the method
            return; 
        }

        //randomly choose the enemy's action (attack, wild attack, defend, 0 is attack, 1 is wild attack, 2 is defend)
        int enemyAction = random.nextInt(3); 

        //enemy attacks
        if (enemyAction == 0) { 

            //enemy attacks player if 0
            int damageDealt = enemy.performAttack(player);

            //reset defense to 2 after attacking
            enemy.resetDefense(); 

            //log the enemy action
            actionLogLabel.setText("The enemy attacked you for " + damageDealt + " damage! Your HP is now: " + player.getHP()); 


            //enemy performs wild attack if 1
        } else if (enemyAction == 1 && !enemy.isWildAttackUsed()) { 

            //they do the wild attack
            int damageDealt = enemy.performWildAttack(player); 

            //log the action
            actionLogLabel.setText("The enemy performed a wild attack for " + damageDealt + " damage! Your HP is now: " + player.getHP()); 

            //reset after use
            enemy.resetWildAttack(); 

             //enemy defends if 2 is rolled
        } else if (enemyAction == 2) {

            //enemy chooses to defend
            enemy.defend(); 

            //log enemy defending action
            actionLogLabel.setText("The enemy chose to defend!"); 
        }
    
        //reset the player's defense state after the enemy's turn ends, then update the label
        player.resetDefense();
        updateStats(); 
    
        //re-enable the buttons after the enemy's turn, being attack defend and wild
        attackButton.setDisable(false); 
        defendButton.setDisable(false); 
        wildAttackButton.setDisable(false); 
    

        //check if player is defeated
        if (!player.isAlive()) { 

            //tell them they were defeated, then show the alert
            actionLogLabel.setText("You have been defeated!"); 
            showDefeatAlert();
        }
    }
    
    //method to show a victory alert
    private void showVictoryAlert() {

        //prompt player to enter their name for scores
        TextInputDialog dialog = new TextInputDialog(""); 

        //set title for victory dialog, the header text, and content text
        dialog.setTitle("Victory!"); 
        dialog.setHeaderText("Congratulations! You defeated the enemy!"); 
        dialog.setContentText("Please enter your name to save your score:"); 

        //show dialog and wait for input
        dialog.showAndWait().ifPresent(name -> { 

            // Save the name and turn count to a text file when input is provided
            saveScore(name, turnCount);
        });
    }

    //method to show a defeat alert
    private void showDefeatAlert() {

        //create an error alert
        Alert alert = new Alert(AlertType.ERROR); 

        //set title for defeat alert, no header alert, the content, and have the action close the window. then exit the application.
        alert.setTitle("Defeat!"); 
        alert.setHeaderText(null); 
        alert.setContentText("You have been defeated!"); 
        alert.setOnHidden(e -> {
            System.exit(0);
        });
        //and obviously, show the alert.
        alert.show();
    }

    //method to save the scores to a txt file
    private void saveScore(String name, int turns) {

        //append  results to the txt.file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt", true))) { 

            //write the score entry, and a new line for the entry
            writer.write(name + " defeated the enemy in " + turns + " turns."); 
            writer.newLine(); 

            //handle any exceptions, and print a stack trace in the event of an error
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    //method to get the players stats as a formated string, then return them
    private String getPlayerStats() {
        return "Player HP: " + player.getHP() + " | ATK: " + player.getATK() + " | DEF: " + player.getDEF(); 
    }

    //the same but for the enemy 
    private String getEnemyStats() {
        return "Enemy HP: " + enemy.getHP() + " | ATK: " + enemy.getATK() + " | DEF: " + enemy.getDEF();
    }

    //then we update it all, for both the enemy and the player
    private void updateStats() {
        playerStatsLabel.setText(getPlayerStats()); 
        enemyStatsLabel.setText(getEnemyStats()); 
    }
}