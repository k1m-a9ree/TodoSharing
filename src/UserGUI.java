import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserGUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        // 앱 실행 (초기 화면 띄우기)
        SwingUtilities.invokeLater(() -> new IntroFrame().setVisible(true));
    }

    // 질문: 버튼을 누르면 다음 창으로 넘어가게 만들어야해 로그인 버튼을 누르면 다이얼로그 뜨고 인증이 되면 다음창으로 넘어가는것처럼
    //      이때 자바에서는 클래스별로 프레임을 두개 만들어서 연결하는게 좋을까 아니면 하나의 프레임에 패널들을 카드레이아웃으로 넘기는게 좋을까?
    // 답변: 일반적인 권장 사항은 2번, 즉 하나의JFrame에 CardLayout을 사용하는 방식.로그인 후 다음 창으로 넘어가는 것은
    //     사실상 **"애플리케이션의 다음 상태/화면으로 전환"**하는 것이며, 별개의 새로운 애플리케이션을 여는 것이 아니기 때문.
    // 평가: 다음창으로 넘어가는 것 일뿐 별개의 새로운 애플리케이션을 여는 것이 아니기 때문이라는 문구에서 동의를 함.
    //      CardLayout를 사용하여 구현하던 중 가시성이 떨어지고 더 복잡해 보여 하나의 클래스에 두개의 프레임을 만들어 연결함.
    //      CardLayout를 사용한 부분이 남아있지만 프로그램 작동에는 이상이 없어 남겨둠.
    // ==========================================
    // 1. 초기 화면 (IntroFrame)
    // ==========================================
    static class IntroFrame extends JFrame {
        private JPanel contentPane;
        public IntroFrame() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 400, 600);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(contentPane);
            contentPane.setLayout(new CardLayout(0, 0));

            // CardLayout 사용을 위한 첫 번째 빈 패널 추가
            JPanel first_panel = new JPanel();
            contentPane.add(first_panel, "first_panel");

            // 두 번째 패널 (실제 보이는 시작 화면)
            JPanel second_panel = new JPanel();
            second_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            contentPane.add(second_panel, "second_panel");
            second_panel.setLayout(null);

            // 시작 시 두 번째 패널을 표시
            CardLayout cl = (CardLayout) contentPane.getLayout();
            cl.show(contentPane, "second_panel");

            // 로그인 버튼
            JButton btnLogin = new JButton("로그인");
            btnLogin.setBounds(44, 283, 126, 42);
            btnLogin.setBackground(new Color(70, 130, 180));
            btnLogin.setForeground(Color.BLACK);
            btnLogin.setFocusPainted(false);
            btnLogin.setFont(new Font("Gothic", Font.BOLD, 14));
            second_panel.add(btnLogin);

            // 타이틀 라벨
            JLabel lblNewLabel = new JLabel("Shared TODO");
            lblNewLabel.setFont(new Font("Arial", Font.BOLD, 32));
            lblNewLabel.setForeground(new Color(50, 50, 150));
            lblNewLabel.setBounds(74, 201, 233, 60);
            second_panel.add(lblNewLabel);

            // 회원가입 버튼
            JButton btnRegister = new JButton("회원가입");
            btnRegister.setForeground(Color.BLACK);
            btnRegister.setFont(new Font("Dialog", Font.BOLD, 14));
            btnRegister.setFocusPainted(false);
            btnRegister.setBackground(new Color(70, 130, 180));
            btnRegister.setBounds(201, 283, 126, 42);
            second_panel.add(btnRegister);

            // 로그인 버튼 클릭 시 리스너
            btnLogin.addActionListener(e -> {
                new LoginDialog(this).setVisible(true);
            });

            // 회원가입 버튼 클릭 시 리스너
            btnRegister.addActionListener(e -> {
                new JoinDialog(this).setVisible(true);
            });
        }

        // 질문: 로그인 버튼 눌렀을때 로그인창을 만들어야하는데 패널을 추가해서 카드레이아웃으로 넘기는 거 말고 다른 방법 없을까?
        // 답변: 다이얼로그를 사용하여 로그인창 구성하면 됨
        // 평가: 코드가 분리돼서 편하다. 카드레이아웃이 아니라서 조금더 로그인 창 처럼 보인다.
        // ==========================================
        // 2. 로그인 다이얼로그 (LoginDialog)
        // ==========================================
        static class LoginDialog extends JDialog {
            private JTextField idField;
            private JPasswordField pwField;
            public LoginDialog(JFrame parent) {
                super(parent, "로그인", true);
                setSize(300, 200);
                setLocationRelativeTo(parent);
                setLayout(new BorderLayout());
                JPanel centerPanel = new JPanel(new GridLayout(2, 2, 5, 10));
                centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                centerPanel.add(new JLabel("아이디 :"));
                idField = new JTextField();
                centerPanel.add(idField);

                centerPanel.add(new JLabel("비밀번호 :"));
                pwField = new JPasswordField();
                centerPanel.add(pwField);

                add(centerPanel, BorderLayout.CENTER);
                JButton loginActionBtn = new JButton("로그인");
                add(loginActionBtn, BorderLayout.SOUTH);

                // 로그인 버튼 클릭 시 동작
                loginActionBtn.addActionListener(e -> {
                    String id = idField.getText();
                    String pw = new String(pwField.getPassword());

                    if (id.isEmpty() || pw.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.");
                        return;
                    }
                    try {
                        Socket socket = new Socket("127.0.0.1", 3000);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                        out.writeObject("LOGIN");
                        out.writeObject(id);
                        out.writeObject(pw);
                        out.flush();

                        String response = (String) in.readObject();

                        if (response.startsWith("SUCCESS")) {
                            JOptionPane.showMessageDialog(this, id + "님 환영합니다!");
                            dispose(); // 로그인 창 닫기
                            parent.setVisible(false); // 초기 화면 닫기
                            // MainAppFrame에 아이디와 스트림들을 전달하며 생성
                            new MainAppFrame(id, out, in).setVisible(true);

                        } else {
                            JOptionPane.showMessageDialog(this, "로그인 실패: " + response);
                            // 실패 시 소켓 닫기
                            socket.close();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "서버 연결 오류: " + ex.getMessage());
                    }
                });
            }
        }

        static class JoinDialog extends JDialog {
            public JoinDialog(JFrame parent) {
                super(parent, "회원가입", true);
                setSize(300, 250);
                setLocationRelativeTo(parent);
                setLayout(new GridLayout(4, 2, 10, 10)); // 4행 2열

                add(new JLabel("  희망 아이디:"));
                JTextField idField = new JTextField();
                add(idField);

                add(new JLabel("  비밀번호:"));
                JPasswordField pwField = new JPasswordField();
                add(pwField);

                add(new JLabel("  이름:"));
                JTextField nameField = new JTextField();
                add(nameField);

                JButton submitBtn = new JButton("가입 완료");
                JButton cancelBtn = new JButton("취소");

                add(submitBtn);
                add(cancelBtn);

                submitBtn.addActionListener(e -> {

                    String id = idField.getText();
                    String pw = new String(pwField.getPassword());
                    if (id.isEmpty() || pw.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "아이디와 비밀번호 입력 필요.");
                        return;
                    }
                    try (Socket socket = new Socket("127.0.0.1", 3000);
                         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                         ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                        out.writeObject("REGISTER");
                        out.writeObject(id);
                        out.writeObject(pw);
                        out.flush();

                        String response = (String) in.readObject();
                        if (response.startsWith("SUCCESS")) {
                            JOptionPane.showMessageDialog(this, "회원가입 성공적");
                            dispose();
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, "서버 연결 오류: " + exception.getMessage());
                    }

                });

                cancelBtn.addActionListener(e -> dispose());
            }
        }
    }

    // ==========================================
    // 3. 메인 애플리케이션 화면 (MainAppFrame)
    // ==========================================
    static class MainAppFrame extends JFrame {

        private List<TodoItem> myOwnTodoList = new ArrayList<>(); // 나의 할 일 (원본)
        private List<TodoItem> currentViewTodoList = new ArrayList<>(); // 현재 화면에 보이는 할 일
        private JPanel todoPanel;
        private JScrollPane scrollPane; // border title 업데이트를 위해 필드로 선언
        private JButton addButton; // 활성화/비활성화를 위해 필드로 선언
        private JTextField todoInputField; // 활성화/비활성화를 위해 필드로 선언
        private String currentUserId;
        private String currentViewOwnerId; // 현재 화면의 주인 (나 or 친구)
        private JLabel timeLabel;
        private JButton saveButton;
        // 수업 시간에 쓴 Date클래스를 사용하여 구성할려 하였으나 잘 되지 않아 질문함
        // 질문: 시간을 나타내는 것을 추가 하고 싶은데 어떻게 하면 돼? 1초마다 레이블의 텍스트를 업데이트하면 되는거 아니야?
        // 답변: 포맷터(Formatter)가 필요하다.Date 객체는 시간을 특정한 시점으로 나타내는 데이터 구조일 뿐, 화면에 표시될 문자열 형식을 가지고 있지 않음.
        //      즉 시분초로 형턔로 바꾸기 위해 필요함.
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        private JMenu friendMenu;

        private ObjectInputStream in;
        private ObjectOutputStream out;

        public MainAppFrame(String userId, ObjectOutputStream out, ObjectInputStream in) {
            super("Shared TODO - " + userId + "님");
            this.currentUserId = userId;
            this.in = in;
            this.out = out;

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);

            // 1. 나의 할 일 초기화 (더미 데이터)
            // 더미 데이터 이제 필요없어서 삭제함

            // 2. 메뉴바 설정
            JMenuBar menuBar = new JMenuBar();
            JMenu myMenu = new JMenu("나의 메뉴");
            JMenuItem myHomeItem = new JMenuItem("나의 할일 보기");

            // '나의 할 일 보기' 클릭 시: 나의 할 일로 화면 전환
            myHomeItem.addActionListener(e -> displayTodoList(this.currentUserId));
            myMenu.add(myHomeItem);

            friendMenu = new JMenu("친구 목록");


            // [추가] 친구 추가 메뉴 아이템
            JMenuItem addFriendItem = new JMenuItem("친구 추가하기...");
            addFriendItem.addActionListener(e -> {
                String fId = JOptionPane.showInputDialog("추가할 친구 ID를 입력하세요:");
                if (fId != null && !fId.trim().isEmpty()) {
                    try {
                        out.writeObject("ADD_FRIEND");
                        out.writeObject(fId);
                        out.flush();
                        String result = (String) in.readObject(); // 서버 응답 대기
                        if ("SUCCESS".equals(result)) {
                            JOptionPane.showMessageDialog(this, "친구가 추가되었습니다. 메뉴를 갱신합니다.");
                            updateFriendMenu(); // 메뉴 갱신
                        } else {
                            JOptionPane.showMessageDialog(this, "친구 추가 실패 (존재하지 않거나 본인입니다).");
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            updateFriendMenu();

            friendMenu.add(addFriendItem);
            friendMenu.addSeparator(); // 구분선

            menuBar.add(myMenu);
            menuBar.add(friendMenu);
            menuBar.add(Box.createHorizontalGlue()); // 메뉴 항목을 왼쪽으로 정렬하고 남은 공간을 채움

            timeLabel = new JLabel(LocalTime.now().format(TIME_FORMATTER));
            timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // 패딩 추가
            menuBar.add(timeLabel);

            setJMenuBar(menuBar);

            // 3. 메인 패널 및 레이아웃 설정
            JPanel mainpanel = new JPanel(new BorderLayout());

            // 4. 할 일 목록 패널 (Center)
            todoPanel = new JPanel();
            todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

            scrollPane = new JScrollPane(todoPanel); // 필드에 할당
            mainpanel.add(scrollPane, BorderLayout.CENTER);

            // 5. 할 일 추가 영역 (South)
            JPanel addTodoPanel = new JPanel(new BorderLayout(5, 5));
            todoInputField = new JTextField(); // 필드에 할당
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

            addButton = new JButton("추가"); // 필드에 할당
            saveButton = new JButton("저장");

            buttonPanel.add(addButton);
            buttonPanel.add(Box.createHorizontalStrut(5));
            buttonPanel.add(saveButton);

            // 전체 패널에 조립
            addTodoPanel.add(todoInputField, BorderLayout.CENTER);
            addTodoPanel.add(buttonPanel, BorderLayout.EAST);

            addTodoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainpanel.add(addTodoPanel, BorderLayout.SOUTH);

            // ------------------------------------------------------------
            // 6. 리스너 설정
            // ------------------------------------------------------------
            // 할 일 추가 (엔터 키 or 추가 버튼)
            ActionListener addAction = e -> addTodoItem(todoInputField);
            addButton.addActionListener(addAction);
            todoInputField.addActionListener(addAction);

            // [NEW] 저장 버튼 리스너 (일괄 저장)
            saveButton.addActionListener(e -> saveAllTodoItems());

            this.add(mainpanel);
            startClock();
            // 7. 초기 화면 렌더링 (나의 할 일)
            displayTodoList(this.currentUserId);
        }

        /**
         * 화면에 표시할 할 일 목록을 바꾸고 UI를 업데이트하는 핵심 메서드
         */
        private void displayTodoList(String ownerId) {
            this.currentViewOwnerId = ownerId;
            List<TodoItem> listToDisplay;
            String title;
            boolean isMyList = ownerId.equals(this.currentUserId);
            try {
                if (isMyList) {
                    out.writeObject("GET");
                    out.flush();

                    // 그 다음 읽어야 서버가 보내준 걸 받음
                    List<TodoItem> serverList = (List<TodoItem>) in.readObject();

                    this.myOwnTodoList.clear();
                    this.myOwnTodoList.addAll(serverList);
                    listToDisplay = this.myOwnTodoList;
                    title = "나의 할 일";
                } else {
                    // 친구 목록일 경우 (더미 데이터 가져오기)
                    listToDisplay = getFriendTodoList(ownerId);
                    title = ownerId + "의 할 일";
                }

                // 뷰 데이터 업데이트
                this.currentViewTodoList = listToDisplay;

                // UI 업데이트: 제목/테두리 변경
                this.scrollPane.setBorder(BorderFactory.createTitledBorder(title));

                // UI 업데이트: 추가 버튼/입력 필드 활성화/비활성화
                addButton.setEnabled(isMyList);
                todoInputField.setEnabled(isMyList);
                todoInputField.setText(isMyList ? "" : "친구의 할 일은 추가할 수 없습니다.");

                // 목록 새로고침
                renderTodoList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "데이터 로딩 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void updateFriendMenu() {
            try {
                // 기존 친구 항목들 제거 (상단 '친구 추가하기'랑 구분선은 남겨야 함)
                // 인덱스 2부터 끝까지 삭제 (0:추가하기, 1:구분선)
                int itemCount = friendMenu.getItemCount();
                for (int i = itemCount - 1; i >= 2; i--) {
                    friendMenu.remove(i);
                }

                out.writeObject("GET_FRIENDS"); // 서버에 요청
                out.flush();
                List<String> friends = (List<String>) in.readObject(); // 받기

                if (friends.isEmpty()) {
                    JMenuItem emptyItem = new JMenuItem("(친구 없음)");
                    emptyItem.setEnabled(false);
                    friendMenu.add(emptyItem);
                } else {
                    for (String friendId : friends) {
                        JMenuItem fItem = new JMenuItem(friendId + "의 할 일 보기");
                        fItem.addActionListener(e -> displayTodoList(friendId)); // 클릭 시 친구 투두 로딩
                        friendMenu.add(fItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 친구의 할 일 목록을 시뮬레이션으로 가져오는 메서드 (실제 서버 통신 부분)
         */
        public List<TodoItem> getFriendTodoList(String friendId) {
            try {
                out.writeObject("GET_FRIEND_TODOS");
                out.writeObject(friendId);
                out.flush();

                // 서버가 친구의 List<TodoItem>을 보내줌
                return (List<TodoItem>) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>(); // 에러 시 빈 리스트
            }
        }

        /**
         * 할 일 추가 기능 (MainAppFrame 내부로 이동하여 스코프 문제 해결)
         */
        private void addTodoItem(JTextField inputField) {
            if (!this.currentViewOwnerId.equals(this.currentUserId)) {
                JOptionPane.showMessageDialog(this, "나의 할 일 목록에서만 추가할 수 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String task = inputField.getText().trim();
            if (!task.isEmpty()) {
                try {
                    out.writeObject("ADD");
                    out.writeObject(task);
                    out.flush();
                    inputField.setText("");

                    displayTodoList(this.currentUserId);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "할 일 추가 중 오류 발생: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "할 일 내용을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void startClock() {
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 현재 시간을 포맷하여 라벨 업데이트
                    String now = LocalTime.now().format(TIME_FORMATTER);
                    timeLabel.setText(now);
                }
            });
            timer.start();
        }

        private void  saveAllTodoItems() {
            // 나의 목록이 아니면 저장 불가
            if (!this.currentViewOwnerId.equals(this.currentUserId)) {
                JOptionPane.showMessageDialog(this, "내 할 일만 저장할 수 있습니다.");
                return;
            }

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

            try {
                out.writeObject("DELETE_ITEMS");
                out.writeObject(selectedItems);
                out.flush();
                displayTodoList(this.currentUserId);
                JOptionPane.showMessageDialog(this, "모든 변경사항이 저장되었습니다.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "저장 중 오류 발생: " + e.getMessage());
            }

        }

        /**
         * 현재 currentViewTodoList의 내용을 화면에 그리는 메서드
         */
        private void renderTodoList() {
            todoPanel.removeAll();
            boolean isMyList = this.currentViewOwnerId.equals(this.currentUserId);

            // 버튼 상태 업데이트: 내 목록일 때만 저장/추가 가능
            saveButton.setEnabled(isMyList);
            addButton.setEnabled(isMyList);
            todoInputField.setEnabled(isMyList);

            for (TodoItem item : currentViewTodoList) {
                JCheckBox checkBox = new JCheckBox(item.getTitle());
                checkBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

                if (!isMyList) {
                    checkBox.setEnabled(false);
                }

                JPanel itemWrapper = new JPanel(new BorderLayout());
                itemWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                itemWrapper.add(checkBox, BorderLayout.WEST);

                todoPanel.add(itemWrapper);
            }

            todoPanel.revalidate();
            todoPanel.repaint();
        }
    }
}