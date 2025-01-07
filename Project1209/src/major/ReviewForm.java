package major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReviewForm {
    private JFrame frame;
    private JTextField bookNameField;
    private JTextField authorNameField;
    private JSpinner ratingSpinner;
    private JTextArea reviewArea;
    private JTextField tagsField;  // 태그 입력 필드 추가
    private Books_zip booksZip;

    public ReviewForm(Books_zip booksZip) {
        this.booksZip = booksZip;

        // 프레임 생성
        frame = new JFrame("독후감 작성하기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);  // 크기 조정
        frame.setLayout(new BorderLayout());

        // 입력 폼 생성
        JPanel formPanel = new JPanel(new GridLayout(5, 2));  // 태그 필드를 포함해 행 추가

        // 도서명 필드
        formPanel.add(new JLabel("도서명 :"));
        bookNameField = new JTextField();
        formPanel.add(bookNameField);

        // 저자명 필드
        formPanel.add(new JLabel("저자명 :"));
        authorNameField = new JTextField();
        formPanel.add(authorNameField);

        // 별점 필드
        formPanel.add(new JLabel("별점 :"));
        SpinnerModel ratingModel = new SpinnerNumberModel(1, 1, 5, 1); // 1부터 5까지 설정
        ratingSpinner = new JSpinner(ratingModel);
        formPanel.add(ratingSpinner);

        // 리뷰 입력 필드
        formPanel.add(new JLabel("내용 :"));
        reviewArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(reviewArea));

        // 태그 필드 추가
        formPanel.add(new JLabel("태그 (#으로 구분):"));
        tagsField = new JTextField();
        formPanel.add(tagsField);

        frame.add(formPanel, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReview();
            }
        });
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 프레임 표시
        frame.setVisible(true);
    }

    private void saveReview() {
        String bookName = bookNameField.getText();
        String authorName = authorNameField.getText();
        int rating = (Integer) ratingSpinner.getValue();
        String reviewContent = reviewArea.getText();
        String tags = tagsField.getText();  // 입력한 태그 받기

        // 필수 입력 필드 검증
        if (bookName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "도서명은 필수 입력 사항입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // `Books_zip` 클래스를 통해 독후감 추가 (파일 저장 포함)
        booksZip.createNewFolder(bookName, authorName, rating, reviewContent, tags);

        // 알림 메시지 및 창 닫기
        JOptionPane.showMessageDialog(frame, "독후감이 저장되었습니다.", "저장 성공", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}
