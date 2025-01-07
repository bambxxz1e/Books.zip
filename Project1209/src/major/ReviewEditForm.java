package major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import major.Folder;

public class ReviewEditForm {
    private JFrame frame;
    private JTextField bookNameField;
    private JTextField authorNameField;
    private JSpinner ratingSpinner;
    private JTextArea reviewArea;
    private JTextField tagsField;
    private Books_zip booksZip;
    private Folder folder;
    private int folderIndex;

    public ReviewEditForm(Books_zip booksZip, Folder folder, int folderIndex) {
        this.booksZip = booksZip;
        this.folder = folder;
        this.folderIndex = folderIndex;

        // 프레임 생성
        frame = new JFrame("독후감 수정하기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());

        // 입력 폼 생성
        JPanel formPanel = new JPanel(new GridLayout(5, 2));

        // 도서명 필드
        formPanel.add(new JLabel("도서명 :"));
        bookNameField = new JTextField(folder.getName());
        formPanel.add(bookNameField);

        // 저자명 필드
        formPanel.add(new JLabel("저자명 :"));
        authorNameField = new JTextField(folder.getAuthorName());
        formPanel.add(authorNameField);

        // 별점 필드
        formPanel.add(new JLabel("별점 :"));
        SpinnerModel ratingModel = new SpinnerNumberModel(folder.getRating(), 1, 5, 1);
        ratingSpinner = new JSpinner(ratingModel);
        formPanel.add(ratingSpinner);

        // 리뷰 입력 필드
        formPanel.add(new JLabel("내용 :"));
        reviewArea = new JTextArea(folder.getReviewContent(), 3, 20);
        formPanel.add(new JScrollPane(reviewArea));

        // 태그 필드
        formPanel.add(new JLabel("태그 (#으로 구분):"));
        tagsField = new JTextField(folder.getTagsAsString());
        formPanel.add(tagsField);

        frame.add(formPanel, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("수정 완료");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEditedReview();
            }
        });
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 프레임 표시
        frame.setVisible(true);
    }

    private void saveEditedReview() {
        String bookName = bookNameField.getText();
        String authorName = authorNameField.getText();
        int rating = (Integer) ratingSpinner.getValue();
        String reviewContent = reviewArea.getText();
        String tags = tagsField.getText();

        // 필수 입력 필드 검증
        if (bookName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "도서명은 필수 입력 사항입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //객체 멤버변수 수정
        folder.setName(bookName);
        folder.setAuthorName(authorName);
        folder.setRating(rating);
        folder.setReviewContent(reviewContent);
        folder.setTags(tags); // 문자열로 태그를 전달

        // UI 갱신 및 파일 저장
        booksZip.updateFolder(folderIndex, folder);

        // 알림 메시지 및 창 닫기
        JOptionPane.showMessageDialog(frame, "독후감이 수정되었습니다.", "수정 완료", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}
