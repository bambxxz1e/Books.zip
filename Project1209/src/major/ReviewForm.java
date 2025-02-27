package major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ReviewForm {
    private JFrame frame;
    private JTextField bookNameField;
    private JTextField authorNameField;
    private JSpinner ratingSpinner;
    private JTextArea reviewArea;
    private JTextField tagsField;  // 태그 입력 필드 추가
    private Books_zip booksZip;
    private String imagePath = null; // 이미지 경로 저장 변수
    private JLabel imagePreviewLabel;

    public ReviewForm(Books_zip booksZip) {
        this.booksZip = booksZip;

        // 프레임 생성
        frame = new JFrame("독후감 작성하기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        // 입력 폼 패널
        JPanel formPanel = new JPanel(new GridLayout(6, 2)); // 7행 2열 -> 6행 2열로 수정 (장르 관련 필드 제거)

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

        // 이미지 선택 버튼
        JButton imageButton = new JButton("이미지 선택");
        imageButton.addActionListener(e -> selectImage());
        formPanel.add(imageButton);

        // 이미지 미리보기 (선택한 파일이 표시됨)
        imagePreviewLabel = new JLabel("이미지 미리보기 없음", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(150, 100));
        formPanel.add(imagePreviewLabel);

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

    // 이미지 선택 기능
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("이미지 선택");
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();

            // 이미지 미리보기 업데이트
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(img));
            imagePreviewLabel.setText(""); // 텍스트 제거
        }
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

        booksZip.createNewFolder(bookName, authorName, rating, reviewContent, tags, imagePath);

        // 알림 메시지 및 창 닫기
        JOptionPane.showMessageDialog(frame, "독후감이 저장되었습니다.", "저장 성공", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }

}
