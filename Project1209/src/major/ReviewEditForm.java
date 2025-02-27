package major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import major.Folder;
import java.util.ArrayList;
import java.util.List;

public class ReviewEditForm {
    private JFrame frame;
    private JTextField bookNameField;
    private JTextField authorNameField;
    private JSpinner ratingSpinner;
    private JTextArea reviewArea;
    private JTextField tagsField;
    private JLabel imagePreviewLabel;
    private String imagePath;
    private Books_zip booksZip;
    private Folder folder;
    private int folderIndex;

    public ReviewEditForm(Books_zip booksZip, Folder folder, int folderIndex) {
        this.booksZip = booksZip;
        this.folder = folder;
        this.folderIndex = folderIndex;
        this.imagePath = folder.getImagePath(); // 기존 이미지 경로 저장

        // 프레임 생성
        frame = new JFrame("독후감 수정하기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        // 입력 폼 생성
        JPanel formPanel = new JPanel(new GridLayout(6, 2));  // 7행 -> 6행으로 수정 (장르 체크박스 제거)

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

        // 이미지 선택 버튼
        JButton imageButton = new JButton("이미지 변경");
        imageButton.addActionListener(e -> selectImage());
        formPanel.add(imageButton);

        // 이미지 미리보기
        imagePreviewLabel = new JLabel("이미지 없음", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(150, 100));
        updateImagePreview(); // 기존 이미지 표시
        formPanel.add(imagePreviewLabel);

        frame.add(formPanel, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("수정 완료");
        saveButton.addActionListener(e -> saveEditedReview());
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 이미지 선택 기능
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("이미지 선택");
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();
            updateImagePreview();
        }
    }

    // 이미지 미리보기 업데이트
    private void updateImagePreview() {
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(img));
            imagePreviewLabel.setText("");
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("이미지 없음");
        }
    }

    // 수정된 독후감 저장
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

        // 객체 멤버변수 수정
        folder.setName(bookName);
        folder.setAuthorName(authorName);
        folder.setRating(rating);
        folder.setReviewContent(reviewContent);
        folder.setTags(tags);
        folder.setImagePath(imagePath);  // 이미지 경로 저장

        // 독후감 업데이트
        booksZip.updateFolder(folderIndex, folder);

        JOptionPane.showMessageDialog(frame, "독후감이 수정되었습니다.", "수정 완료", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}
