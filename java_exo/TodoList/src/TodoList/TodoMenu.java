package TodoList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoMenu {
    public class Singleton {
        private static Scanner instance;

        private Singleton() {
        }
        public static Scanner getInstance() {
            if (instance == null) {
                instance = new Scanner(System.in);
            }
            return instance;
        }
    }

    public static void main(String[] args) throws SQLException {
        Scanner scanner = Singleton.getInstance();

        System.out.println("=====TODO Menu=====");
        System.out.println("1 : Add a new task");
        System.out.println("2 : Find a task");
        System.out.println("3 : Show all task");
        System.out.println("4 : Update a task");
        System.out.println("5 : Delete a task");
        System.out.println("6 : Quit");
        System.out.println("Enter your choice :");

        int choice = scanner.nextInt();

        do {
            switch (choice){
                case 1:
                    System.out.println("Enter the task details:");
                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Title: ");
                    String title = scanner.nextLine();

                    System.out.print("Description: ");
                    String description = scanner.nextLine();

                    System.out.print("Deadline (YYYY-MM-DD HH:MM:SS): ");
                    String deadlineString = scanner.nextLine();
                    Timestamp deadline = Timestamp.valueOf(deadlineString);

                    System.out.print("Priority (1-5): ");
                    int priority = scanner.nextInt();

                    System.out.print("Done (true/false): ");
                    boolean done = scanner.nextBoolean();

                    insertTodo(id, title, description, deadline, priority, done);
                    break;

                case 2:
                    System.out.println("Entre your choice id");
                    int choiceId = scanner.nextInt();
                    System.out.println(TodoMenu.findTaskById(choiceId));
                    break;

                case 3:
                    System.out.println(TodoMenu.findAllUsers());
                    break;

                case 4:
                    System.out.println("Enter the task ID to update:");
                    int taskIdToUpdate = scanner.nextInt();
                    scanner.nextLine();
                    Task taskToUpdate = findTaskById(taskIdToUpdate);
                    if (taskToUpdate != null) {
                        System.out.print("Enter the new title: ");
                        String newTitle = scanner.nextLine();

                        System.out.print("Enter the new description: ");
                        String newDescription = scanner.nextLine();

                        System.out.print("Enter the new deadline (YYYY-MM-DD HH:MM:SS): ");
                        String newDeadlineString = scanner.nextLine();
                        Timestamp newDeadline = Timestamp.valueOf(newDeadlineString);

                        System.out.print("Enter the new priority (1-5): ");
                        int newPriority = scanner.nextInt();

                        System.out.print("Enter the new done status (true/false): ");
                        boolean newDone = scanner.nextBoolean();

                        updateTask(taskIdToUpdate, newTitle, newDescription, newDeadline, newPriority, newDone);
                        System.out.println("Task updated successfully.");
                    } else {
                        System.out.println("Task not found.");
                    }
                    break;

                case 5:
                    System.out.println("Enter the task ID to delete:");
                    int taskIdToDelete = scanner.nextInt();
                    boolean deleted = deleteTask(taskIdToDelete);
                    if (deleted) {
                        System.out.println("Task deleted successfully.");
                    } else {
                        System.out.println("Task not found.");
                    }
                    break;

                case 6:
                    System.out.println("GoodBye");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            if (choice != 6){
                System.out.println("Enter your choice : ");
                choice = scanner.nextInt();
            }else break;
        }while (choice != 6);
    }

    private static final Connection connection;

    static {
        ConnectionToDataBase db = new ConnectionToDataBase();
        connection = db.createConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertTodo(int id, String title, String description, Timestamp deadline, int priority, boolean done) throws SQLException {
        try {
            String sql = "INSERT INTO \"todos\" (id, title, description, deadline, priority, done) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setTimestamp(4, deadline);
            statement.setInt(5, priority);
            statement.setBoolean(6, done);
            statement.executeUpdate();
            System.out.println("Insert successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Task findTaskById(int choiceId) {
        try {
            String sql = "SELECT * FROM todos WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, choiceId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getTimestamp("deadline"),
                        resultSet.getInt("priority"),
                        resultSet.getBoolean("done")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Task> findAllUsers() {
        List<Task> taskList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM todos";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getTimestamp("deadline"),
                        resultSet.getInt("priority"),
                        resultSet.getBoolean("done")
                );
                taskList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList;
    }
    public static void updateTask(int taskId, String newTitle, String newDescription, Timestamp newDeadline, int newPriority, boolean newDone) {
        try {
            String sql = "UPDATE todos SET title = ?, description = ?, deadline = ?, priority = ?, done = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newTitle);
            statement.setString(2, newDescription);
            statement.setTimestamp(3, newDeadline);
            statement.setInt(4, newPriority);
            statement.setBoolean(5, newDone);
            statement.setInt(6, taskId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteTask(int taskId) {
        try {
            String sql = "DELETE FROM todos WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, taskId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
