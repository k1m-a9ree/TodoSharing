import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TodoServer {
    private static final int PORT = 3000; // 포트 변수
    private static final String DB_FILE = "users_db.dat"; // 데이터 베이스 파일

    private static Map<String, User> allUsers = new ConcurrentHashMap<>(); // 유저 불러와서 여기다가 저장함

    public static void main(String[] args) {
        loadUsers();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버 시작 (Port: " + PORT + ")");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start(); // 스레드 생성 (Runnable 만들었음)
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

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

    static class ClientHandler implements Runnable { // 클라 핸들러
        private Socket socket;
        private User currentUser; // 현재 접속한 유저

        public ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // 인증 루프
                while (currentUser == null) {
                    String type = (String) in.readObject(); // 가입 또는 로그인
                    String id = (String) in.readObject();
                    String pw = (String) in.readObject();

                    /*
                    대충 id랑 pw 입력받아서 가입이나 로그인 함
                    출력할때 FAIL이나 SUCCESS가 앞에 있는 이유는, 클라이언트에서 이걸로 성공 실패 여부 확인함
                    (startsWith는 첨봐서 당황했슨;;)
                     */
                    if (type.equals("REGISTER")) { // 가입
                        if (allUsers.containsKey(id)) {
                            out.writeObject("FAIL:이미 존재하는 아이디입니다.");
                        } else {
                            allUsers.put(id, new User(id, pw));
                            saveUsers();
                            out.writeObject("SUCCESS:회원가입 완료. 로그인해주세요.");
                        }
                    } else if (type.equals("LOGIN")) { // 로그인
                        User user = allUsers.get(id);
                        if (user != null && user.getPassword().equals(pw)) {
                            currentUser = user; // 로그인 성공!
                            out.writeObject("SUCCESS");
                        } else {
                            out.writeObject("FAIL:아이디 또는 비번이 틀립니다.");
                        }
                    }
                    out.flush();
                }

                System.out.println(">>> 로그인 성공: " + currentUser);

                // 업무 루프
                while (true) {
                    String cmd = (String) in.readObject();

                    if (cmd.equals("GET")) { // 투두리스트 보내기
                        out.reset();
                        out.writeObject(new ArrayList<>(currentUser.getTodoList()));
                        out.flush();

                    } else if (cmd.equals("ADD")) { // 투두 추가
                        String title = (String) in.readObject();
                        currentUser.getTodoList().add(new TodoItem(title));
                        saveUsers();

                    } else if (cmd.equals("DELETE_ITEMS")) { // 저장 버튼 (체크된 리스트 삭제)
                        List<String> titlesToDelete = (List<String>) in.readObject();
                        // 제목이 일치하면 삭제 (완료 처리)
                        currentUser.getTodoList().removeIf(item -> titlesToDelete.contains(item.getTitle()));
                        saveUsers();

                    } else if (cmd.equals("GET_FRIENDS")) { // 친구 목록 보내기
                        out.reset();
                        out.writeObject(currentUser.listFriends());
                        out.flush();

                    } else if (cmd.equals("ADD_FRIEND")) { // 친구 추가
                        String friendId = (String) in.readObject();
                        if (allUsers.containsKey(friendId) && !friendId.equals(currentUser.getUsername())) {
                            currentUser.addFriend(friendId);
                            saveUsers();
                            out.writeObject("SUCCESS");
                        } else {
                            out.writeObject("FAIL");
                        }
                        out.flush();

                    } else if (cmd.equals("GET_FRIEND_TODOS")) { // 친구 투두 엿보기
                        String friendId = (String) in.readObject();
                        User friend = allUsers.get(friendId);
                        out.reset();
                        if (friend != null) {
                            out.writeObject(new ArrayList<>(friend.getTodoList()));
                        } else {
                            out.writeObject(new ArrayList<>());
                        }
                        out.flush();

                    } else if (cmd.equals("EXIT")) { // 나가기
                        break;
                    }
                    out.flush();
                }

            } catch (Exception e) {
                // 접속 종료
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println(">>> 접속 종료");
            }
        }
    }
}