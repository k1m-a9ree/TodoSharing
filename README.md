# Todo Sharing
***

## 단순 로직

### 연결
1. 서버소켓 만들어서 accept() 무한반복하기
2. 클라에서 소켓 만들면
3. 서버에서 소켓을 accept로 받아서 스레드로 start()
4. 주고받기

### 로그인, 가입
1. 클라에서 요청 보냄 (로그인: LOGIN, 가입: REGISTER)
2. 클라에서 id랑 pw도 보냄
3. 서버에서 받아서 로그인이나 가입 시도함
4. 성공하면 message 앞에 SUCCESS, 실패하면 앞에 FAIL 붙여서 보냄
5. 클라에서 앞에 문자 인식해서 성공이면 다음 페이지로 넘어감

### API
1. 클라에서 명령어랑 같이 요청 보냄(명령어: GET, ADD, DELETE_ITEMS, GET_FRIENDS, ADD_FRIEND, GET_FRIENDS_TODOS)
2. 서버에서 명령에 따라 처리함
3. 클라는 명령어에 맞는 데이터를 추가로 보냄
4. 서버는 그걸로 처리함

***

## 클래스 간단 요약
### User

```java
class User {
    // 필드
    String username;
    String password;
    List<TodoItem> todoList;
    private Set<String> friends;

    // 메소드
    public String getUsername();
    public String getPassword();
    public List<TodoItem> getTodoList();
    public boolean addFriend();
    public List<String> listFriends(String friendId);
}

```

### UserGUI

```java
public class UserGUI {
    // 메소드
    public static void main(String[] args);

    // 중첩 클래스
    static class IntroFrame extends JFrame {
        // 필드
        private JPanel contentPane;

        // 메소드
        public IntroFrame();
    }

    // 중첩 클래스
    static class LoginDialog extends JDialog {
        // 필드
        private JTextField idField;
        private JPasswordField pwField;

        // 메소드
        public LoginDialog(JFrame parent);
    }

    // 중첩 클래스
    static class JoinDialog extends JDialog {
        // 메소드
        public JoinDialog(JFrame parent);
    }

    // 중첩 클래스
    static class MainAppFrame extends JFrame {
        // 필드
        private List<TodoItem> myOwnTodoList;
        private List<TodoItem> currentViewTodoList;
        private JPanel todoPanel;
        private JScrollPane scrollPane;
        private JButton addButton;
        private JTextField todoInputField;
        private String currentUserId;
        private String currentViewOwnerId;
        private JLabel timeLabel;
        private JButton saveButton;
        private static final DateTimeFormatter TIME_FORMATTER;
        private JMenu friendMenu;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        // 메소드
        public MainAppFrame(String userId, ObjectOutputStream out, ObjectInputStream in);
        private void displayTodoList(String ownerId);
        private void updateFriendMenu();
        public List<TodoItem> getFriendTodoList(String friendId);
        private void addTodoItem(JTextField inputField);
        private void startClock();
        private void saveAllTodoItems();
        private void renderTodoList();
    }
}
```

### TodoServer

```java
public class TodoServer {
    // 필드
    private static final int PORT;
    private static final String DB_FILE;
    private static Map<String, User> allUsers;

    // 메소드
    public static void main(String[] args);
    private static synchronized void loadUsers();
    private static synchronized void saveUsers();

    // 중첩 클래스
    static class ClientHandler implements Runnable {
        // 필드
        private Socket socket;
        private User currentUser;

        // 메소드
        public ClientHandler(Socket socket);
        public void run();
    }
}
```

***

## 사용 기술

### GUI
UserGUI.java 참고

### 멀티 스레딩
TodoServer.java 각 사용자마다 동시에 처리하기 위해 사용
```java
new Thread(new ClientHandler(socket)).start(); // 스레드 생성 (Runnable 만들었음)

static class ClientHandler implements Runnable {
    public void run();
}
```

### 네트워크
클라이언트: UserGUI.java, 서버: TodoServer.java 참고

### 파일 입출력
TodoServer.java 서버를 닫을 때 유저 정보를 저장하고, 서버를 열 때 유저 정보를 가져오기 위해 사용
```java
private static synchronized void loadUsers() { // 데베에서 유저 불러와서 Map에 넣기
        File file = new File(DB_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            allUsers = (Map<String, User>) ois.readObject();
            System.out.println(">>> 유저 DB 로드 완료 (" + allUsers.size() + "명)");
        } catch (Exception e) { allUsers = new ConcurrentHashMap<>(); }
    }

    private static synchronized void saveUsers() { // 데베에 다시 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(allUsers); // 맵 전체 저장
        } catch (IOException e) { e.printStackTrace(); }
    }
```

### 컬렉션
User.java 친구를 조회하고 추가하기 위해 Set 사용
```java
private Set<String> friends = new HashSet<>();
```

TodoServer.java 유저 아이디로 유저 객체를 찾기 위해 Map 사용
```java
private static Map<String, User> allUsers = new ConcurrentHashMap<>();
```

### 함수형 프로그래밍
UserGUI.java 투두 리스트에서 체크된 항목(완료한 항목)의 이름만 모으기 위해 stream 사용
```java
List<String> selectedItems = Arrays.stream(todoPanel.getComponents())
        .filter(comp -> comp instanceof JPanel)
        .map(comp -> (JPanel) comp)
        .map(panel -> {
            for (Component comp: panel.getComponents()) {
                if (comp instanceof JCheckBox) {
                    return (JCheckBox) comp;
                }
            }
            return null;
        })
        .filter(comp -> comp != null)
        .filter(JCheckBox::isSelected)
        .map(JCheckBox::getText)
        .toList();
```