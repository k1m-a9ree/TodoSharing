import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class TodoClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 3000;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP, PORT); // [13주차 PDF p.18]
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // 인증 화면
            boolean isLoggedIn = false;
            while (!isLoggedIn) {
                System.out.println("\n=== 투두리스트 인증 ===");
                System.out.println("1.로그인  2.회원가입  3.종료");
                System.out.print("선택>> ");
                String menu = sc.nextLine();

                if (menu.equals("3")) return;

                System.out.print("ID: "); String id = sc.nextLine();
                System.out.print("PW: "); String pw = sc.nextLine();

                if (menu.equals("1")) out.writeObject("LOGIN");
                else out.writeObject("REGISTER");

                out.writeObject(id);
                out.writeObject(pw);
                out.flush();

                String response = (String) in.readObject(); // 결과 수신
                if (response.startsWith("SUCCESS")) {
                    if (menu.equals("1")) isLoggedIn = true; // 로그인 성공 시 루프 탈출
                    else System.out.println(">>> " + response);
                } else {
                    System.out.println(">>> " + response);
                }
            }

            // 메인 화면
            while (true) {
                // 서버에서 받아서 출력
                List<TodoItem> list = (List<TodoItem>) in.readObject();

                System.out.println("\n=== 나의 할 일 목록 ===");
                if (list.isEmpty()) System.out.println("(비어있음)");
                for (int i = 0; i < list.size(); i++)
                    System.out.println(i + ". " + list.get(i));

                System.out.println("\n1.추가 2.삭제 3.완료체크 4.종료");
                System.out.print("선택>> ");
                String menu = sc.nextLine();

                if (menu.equals("1")) {
                    out.writeObject("ADD");
                    System.out.print("할 일 내용: ");
                    out.writeObject(sc.nextLine());
                } else if (menu.equals("2")) {
                    out.writeObject("DELETE");
                    System.out.print("삭제할 번호: ");
                    out.writeObject(Integer.parseInt(sc.nextLine()));
                } else if (menu.equals("3")) {
                    out.writeObject("TOGGLE");
                    System.out.print("체크할 번호: ");
                    out.writeObject(Integer.parseInt(sc.nextLine()));
                } else if (menu.equals("4")) {
                    out.writeObject("EXIT");
                    break;
                }
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("서버 통신 오류: " + e.getMessage());
        }
    }
}