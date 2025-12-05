# 텀 프로젝트 보고서 (공유 투두리스트)


## 목차
* 프로젝트 소개
* 팀원과 팀원의 역할
* 프로그램 로직
* 프로젝트에서 사용된 기술
* 프로그램에 대한 평가
* 프로젝트 진행에 대한 개인적 평가
* 깃허브


## 프로젝트 소개
시중에 나와있는 할 일 목록 앱에는 아이폰의 '미리알림', 그리고 갤럭시의 '리마인더'가 있다. 이 앱들은 사용자가 할 일 목록을 만들어 할 일의 완료여부를 관리할 수 있게 하지만, 다른 사용자와 공유는 할 수가 없어 친구나 동료가 어떤 일을 우선적으로 해야하는지, 하고있는지 알 수 없다. 그러하여 우리는 다른 사용자와 할 일 목록을 공유할 수 있는 **_'공유 투두리스트'_** 를 개발하기로 하였다.<br>
사용자를 인식하기 위한 로그인과 회원가입 기능, 투두리스트를 작성하여 서버에 저장하고 완료된 할 일을 삭제하는 기능, 그리고 친구를 추가하고 친구의 투두리스트를 볼 수 있는 기능이 있다.


## 팀원과 팀원의 역할

### 각 팀원의 역할
* 김동희: 프로그램의 서버 개발을 담당하였다. 
* 장강민: 프로그램의 GUI 개발을 담당하였다.
* 임은균: User 클래스 개발을 담당하였다.

### 개발 타임테이블
![타임테이블](./ReportResources/timetable.png)

### 회의 상세 기록

#### 첫 번째 회의
<!-- ![첫 번째 회의 사진](./ReportResources/first_meeting.jpeg) -->
첫 번째 회의는 11월 19일, 도서관 크리에이티브실에서 진행하였다. 팀원 모두가 참석하였다.<br>
주제 선정과 기획, 팀원 역할 분담에 대해 이야기하였다.

#### 두 번째 회의
<!-- ![두 번째 회의 사진](./ReportResources/second_meeting.jpeg) -->
두 번째 회의는 11월 25일, 공대5호관 창의설계실에서 진행하였다. 팀원 모두가 참석하였다.<br>
각 팀원이 작성해온 코드를 병합하기 위해 각자의 코드 설명과 병합 진행과정에 대한 논의를 하였다.

#### 세 번째 회의
<!-- ![세 번째 회의 사진](./ReportResources/third_meeting.jpeg) -->
세 번째 회의는 12월 3일, 공대5호관 창의설계실에서 진행하였다. 팀원 모두가 참석하였다.<br>
최종 결과 테스트와 수정 사항 확인, 그리고 보고서에 들어갈 내용을 생각하였다.


## 프로그램 로직

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


## 프로젝트에 사용된 기술

### GUI

> UserGUI.java

IntroFrame에서 시작해, JDialog에서 회원가입이나 로그인을 한 후, MainAppFrame으로 넘어감 
actionListner로 버튼 클릭 시 그에 맞는 동작을 할 수 있도록 함 

```java
static class IntroFrame extends JFrame {
    ...
}
static class LoginDialog extends JDialog {
    ...
}
static class JoinDialog extends JDialog {
    ...
}
static class MainAppFrame extends JFrame {
    ...
}
```

```java
// 로그인 버튼
JButton btnLogin = new JButton("로그인");
btnLogin.setBounds(44, 283, 126, 42);
second_panel.add(btnLogin);

// 로그인 버튼 클릭 시 리스너
btnLogin.addActionListener(e -> {
    // 로그인 다이얼로그 생성 및 표시
    new LoginDialog(this).setVisible(true);
});
```

### 멀티스레딩

> TodoServer.java 

각 사용자마다 동시에 처리하기 위해 사용

```java
new Thread(new ClientHandler(socket)).start(); // 스레드 생성해서 시작

static class ClientHandler implements Runnable { // Runnable 구현
    public void run() {
        ...
    }
}
```

### 네트워크

> TodoServer.java

클라이언트와 통신하기 위해 사용<br>
서버 소켓을 생성한 후 accept()로 클라이언트의 소켓을 받아서 연결함

```java
// TodoServer.java
try (ServerSocket serverSocket = new ServerSocket(PORT)) {
    System.out.println("서버 시작 (Port: " + PORT + ")");

    while (true) {
        Socket socket = serverSocket.accept(); // 클라이언트 접속 대기
        new Thread(new ClientHandler(socket)).start(); // 연결되면 스레드 생성
    }
} catch (IOException e) { 
    e.printStackTrace(); 
}
```

> UserGUI.java

서버랑 통신하기 위해 사용<br>
소켓으로 서버에 연결 요청, ObjectoutputStream과 ObjectInputStream으로 서버와 송수신함

```java
// UserGUI.java의 LoginDialog
try {
    Socket socket = new Socket("127.0.0.1", 3000); // 서버에 연결
    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

    out.writeObject("LOGIN"); // "LOGIN" 명령어 전송
    out.writeObject(id);      // 아이디 전송
    out.writeObject(pw);      // 비밀번호 전송
    out.flush();

    String response = (String) in.readObject(); // 서버 응답 수신

    if (response.startsWith("SUCCESS")) {
        // 로그인 성공 처리
    }
    ...
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "서버 연결 오류: " + ex.getMessage());
}
```

### 파일 입출력

> TodoServer.java 

서버를 닫을 때 유저 정보를 저장하고, 서버를 열 때 유저 정보를 가져오기 위해 사용

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

> User.java

User객체를 파일에 저장하기 위해 Serializable 인터페이스 구현

```java
public class User implements Serializable {
    private static final long serialVersionUID = 2L;
}
```

> TodoItem.java

TodoItem객체를 파일에 저장하기 위해 Serializable 인터페이스 구현

```java
public class TodoItem implements Serializable {
    private static final long serialVersionUID = 1L;
}
```


### 컬렉션

> User.java 

친구를 조회하고 추가하기 위해 Set 사용, TodoItem을 넣기 위한 List 사용

```java
private Set<String> friends = new HashSet<>();
List<TodoItem> todoList = new ArrayList<>();
```

> TodoServer.java 

유저 아이디로 유저 객체를 찾기 위해 Map 사용

```java
private static Map<String, User> allUsers = new ConcurrentHashMap<>();
```

### 함수형 프로그래밍

> UserGUI.java => MainAppFrame 클래스 => renderTodoList 메서드

투두 리스트에서 체크된 항목(완료한 항목)의 이름만 모으기 위해 stream 사용

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


## 프로그램에 대한 평가

### 잘한 점

1. 서버와 클라이언트가 매끄럽고 원활하게 통신이 된다.
2. 버그 없이 원하는대로 잘 작동한다.

### 아쉬운 점 & 개선할 점

1. 어떤 프로그램이 데이터베이스에 유저 비밀번호를 그대로 저장하는가? 다음부터는 해시함수를 적극 활용하거나 비밀번호를 암호화를 해서 저장하도록 하자.
2. 친구추가를 친구 동의 없이 해버리면, 그냥 남의 집에 무단침입 하는 것과 다를 것이 없다. 
친구 요청&수락 기능을 추가하여 상호동의하에 친구 추가가 이루어지면 좋을 것 같다.
3. 할 일에 우선순위를 유저가 직접 매겨서, 보여줄 때 할 일 목록을 우선순위로 정렬해서 보여주거나 가장 중요한 할 일을 보여주면 좋을 것 같다.
PriorityQueue나 Collections.sort()를 사용하면 될 것 같다.


## 프로젝트 진행에 대한 개인적 견해

사실 정말 잘 진행된 이상적인 조별 과제였다고 생각한다. 
다들 회의를 하는데 빠짐없이 적극적이며 다음 회의 전까지 할 일을 다 해오기도 하고 그래서 좋았다.
또한 역할 분담을 적절하게 잘 나눠서, 각자의 최선을 다해서 개발할 수 있었다고 생각한다.<br>
그리고 프로그래밍 실력도 상승한 것 같다.
아무래도 그냥 ppt보고 암기하고 실습 따라하는 것 보다는, 직접 생각해서 필요한 곳에 활용해보는게 실력 상승에 확실히 도움 된다는 것을 깨달았다.

## 깃허브

다음은 깃허브 링크이다

[깃허브 TodoSharing](https://github.com/k1m-a9ree/TodoSharing)