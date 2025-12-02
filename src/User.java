import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private static final long serialVersionUID = 2L;

    String username;
    String password;

    // 각 사용자별 할일 목록 리스트
    List<TodoItem> todoList = new ArrayList<>();

    public List<TodoItem> getTodoList() { return todoList; }

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    // HashSet으로 친구 아이디 저장
    private Set<String> friends = new HashSet<>();

    // 친구 추가 (중복 자동 처리)
    public boolean addFriend(String friendId) {
        if (friendId == null || friendId.isBlank()) return false;
        return friends.add(friendId); // 신규 추가면 true, 이미 있으면 false
    }

    // 친구 삭제
    public boolean removeFriend(String friendId) {
        return friends.remove(friendId);
    }

    // 친구 여부 확인
    public boolean isFriend(String friendId) {
        return friends.contains(friendId);
    }

    // 모든 친구 목록 반환 (읽기 전용 복사본)
    public List<String> listFriends() {
        return new ArrayList<>(friends);
    }

    // 친구 수
    public int size() {
        return friends.size();
    }

    // 전체 출력 (콘솔용 헬퍼)
    public void printFriends() {
        if (friends.isEmpty()) {
            System.out.println("친구가 없습니다.");
            return;
        }
        System.out.println("친구 목록:");
        int i = 1;
        for (String f : friends) {
            System.out.println(i++ + ". " + f);
        }
    }


}