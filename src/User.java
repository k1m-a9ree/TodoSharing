import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private static final long serialVersionUID = 2L;
    /*
     [내가 했던 질문]
     - "User 클래스를 파일 저장/불러오기 할 때 오류가 나는데 Serializable을 어떻게 구현해야 해?"
     - "serialVersionUID는 꼭 필요한가?"

     [답변]
     - "파일 저장/로드를 위해서는 반드시 Serializable 인터페이스가 필요하고,
        클래스 구조가 바뀌면 파일 읽기 과정에서 충돌이 날 수 있으므로
        일관성 유지를 위해 serialVersionUID를 수동으로 지정하는 게 좋다."

     [완성된 코드에 대한 평가]
     - 직렬화 안정성을 잘 확보했고, 향후 데이터 저장 기능 확장에 충분히 견고함.
    */


    String username;
    String password;

    /*
     [내가 했던 질문]
     - "할 일 목록을 그냥 String 리스트로 쓰니까 확장성이 너무 떨어지는데,
        각각에 날짜, 우선순위 같은 걸 나중에 붙일 수 있게 구조화하는 방법은?"
     - "TodoItem 클래스를 만들어서 리스트로 갖고 있으면 되는 건가?"

     [답변]
     - "맞다. 확장 가능하게 만들고 싶으면 TodoItem 클래스로 묶고
        User 내부에서는 List<TodoItem>으로 관리하는 것이 가장 좋은 구조다."

     [완성된 코드에 대한 평가]
     - TodoItem 객체 단위로 관리하여 재사용성과 확장성을 확보했음.
    */
    List<TodoItem> todoList = new ArrayList<>();
    public List<TodoItem> getTodoList() { return todoList; }

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    /*
     [내가 했던 질문]
     - "친구 목록은 중복 저장되면 안 되는데, 리스트로 하면 관리가 너무 어려워…"
     - "중복 없이, 빠르게 추가/삭제/탐색 할 수 있는 자료구조 없을까?"

     [답변]
     - "중복을 자동으로 걸러주는 HashSet을 사용하는 것이 가장 효율적이다.
        contains(), add(), remove() 모두 평균 O(1)으로 처리된다."

     [완성된 코드에 대한 평가]
     - 중복 방지, 빠른 탐색 등 요구사항에 가장 적합한 자료구조 선택.
    */
    private Set<String> friends = new HashSet<>();

    /*
     [내가 했던 질문]
     - "친구 추가할 때 공백이나 null이 들어오면 어떻게 거르지?"
     - "이미 있는 값이 들어오면 어떻게 처리해야 해?"

     [답변]
     - "HashSet.add()는 성공하면 true, 이미 있으면 false를 반환한다.
        따라서 그대로 return하면 로직이 깨끗해진다."

     [완성된 코드에 대한 평가]
     - 예외 처리 및 중복 처리까지 깔끔하게 해결된 구조.
    */
    public boolean addFriend(String friendId) {
        if (friendId == null || friendId.isBlank()) return false;
        return friends.add(friendId);
    }

    public boolean removeFriend(String friendId) {
        return friends.remove(friendId);
    }
    public boolean isFriend(String friendId) {
        return friends.contains(friendId);
    }

    /*
     [내가 했던 질문]
     - "외부에서 friends를 그대로 리턴하면 외부에서 수정해버릴 수 있는데,
        안전하게 읽기 전용으로 전달하려면 어떻게 해야 해?"

     [답변]
     - "새로운 ArrayList<>(friends) 를 만들어 복사본을 넘겨라.
        그러면 원본 friends는 안전하게 보호된다."

     [완성된 코드에 대한 평가]
     - 캡슐화 원칙을 잘 지킨 구현.
    */
    public List<String> listFriends() {
        return new ArrayList<>(friends);
    }

    public int size() {
        return friends.size();
    }

    /*
     [내가 했던 질문]
     - "콘솔에서 친구 목록을 출력하는 헬퍼 메서드를 만들 수 있을까?
        없을 때는 없다고 알려주고, 있을 때는 번호 붙여서 보여주면 좋겠어."

     [답변]
     - "친구 목록이 비었으면 안내 문구를 출력하고,
        for-each 문으로 번호를 붙여 출력하면 된다."

     [완성된 코드에 대한 평가]
     - 디버깅/테스트 때 매우 유용한 간단·직관적 유틸리티.
    */
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