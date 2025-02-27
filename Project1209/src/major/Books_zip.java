package major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.ParseException; // 이 부분 추가
import java.util.*;
import java.util.Date;
import java.io.*; // 파일 입출력 클래스들
import javax.swing.table.AbstractTableModel;
import major.Folder;

public class Books_zip {
	private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "reviews.txt";
	
    private JFrame frame;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private ArrayList<Folder> folders; // Folder 객체 리스트
    private FolderTableModel tableModel;
    private JButton newFolderButton; // 독후감 작성하기 버튼을 멤버 변수로 선언
    private JButton tagSearchButton; // 태그 검색 버튼

    // 생성자: Books_zip UI 초기화
    public Books_zip() {
        // 프레임 생성
        frame = new JFrame("Books_zip");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // 좌측 패널 생성 (폴더 리스트)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        leftPanel.setPreferredSize(new Dimension(150, 400)); // 좌측 패널 너비 지정
        frame.add(leftPanel, BorderLayout.WEST);

        // 폴더를 저장할 리스트 초기화
        folders = new ArrayList<>();

        // 중앙 패널 생성
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // 상단 "새로 만들기" 버튼
        newFolderButton = new JButton("+ 독후감 작성하기"); // 멤버 변수로 선언된 버튼 초기화
        newFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 독후감 작성 폼 화면 열기
                openReviewForm();
            }
        });
        leftPanel.add(newFolderButton);
        
        // "태그 검색" 버튼 추가
        tagSearchButton = new JButton("태그 검색");
        tagSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 태그 검색 창 열기
                openTagSearchForm();
            }
        });
        leftPanel.add(tagSearchButton);

        // 중앙 테이블 (폴더명 및 수정 날짜 표시)
        tableModel = new FolderTableModel(folders);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 테이블 클릭 리스너 추가 (독후감 내용 보기)
        addFolderClickListener(table);

        // 정렬 버튼 패널 생성
        JPanel sortPanel = new JPanel(new FlowLayout());
        JButton sortByNameButton = new JButton("가나다순");
        JButton sortByRecentButton = new JButton("최근 생성 순");
        JButton sortByOldestButton = new JButton("오래된 순");
        JButton sortByRatingButton = new JButton("별점 순"); // 별점 순 정렬 버튼

        // 가나다순 정렬 버튼 클릭 시
        sortByNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortFoldersByName();
            }
        });

        // 최근 생성 순 정렬 버튼 클릭 시
        sortByRecentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortFoldersByRecent();
            }
        });

        // 오래된 순 정렬 버튼 클릭 시
        sortByOldestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortFoldersByOldest();
            }
        });

        // 별점 순 정렬 버튼 클릭 시
        sortByRatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortFoldersByRating();
            }
        });

        sortPanel.add(sortByNameButton);
        sortPanel.add(sortByRecentButton);
        sortPanel.add(sortByOldestButton);
        sortPanel.add(sortByRatingButton);

        centerPanel.add(sortPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        // 프로그램 시작 시 파일 데이터 불러오기
        loadReviewsFromFile();

        // 프레임 표시
        frame.setVisible(true);
    }
    
    // 태그 검색 창을 여는 메서드
    private void openTagSearchForm() {
        String tag = JOptionPane.showInputDialog(frame, "검색할 태그를 입력하세요 (예: #재밌는, #웃긴)");

        if (tag != null && !tag.trim().isEmpty()) {
            searchByTag(tag.trim());
        }
    }

    // 입력된 태그로 검색해서 결과 표시
    private void searchByTag(String tag) {
        ArrayList<Folder> filteredFolders = new ArrayList<>();

        // 태그로 필터링
        for (Folder folder : folders) {
            if (folder.getTagsAsString().contains(tag)) {
                filteredFolders.add(folder);
            }
        }

        // 검색된 결과가 있으면 새로운 창에 테이블로 표시
        if (filteredFolders.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "해당 태그를 가진 독후감이 없습니다.");
        } else {
            // 새로운 창 열기
            JFrame tagSearchFrame = new JFrame("태그 검색 결과");
            tagSearchFrame.setSize(400, 200);
            tagSearchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 중앙 패널
            JPanel tagSearchPanel = new JPanel(new BorderLayout());

            // 테이블 모델 설정
            FolderTableModel tagTableModel = new FolderTableModel(filteredFolders);
            JTable table = new JTable(tagTableModel);
            JScrollPane tableScrollPane = new JScrollPane(table);
            tagSearchPanel.add(tableScrollPane, BorderLayout.CENTER);

            // 테이블 클릭 리스너 추가 (독후감 내용 보기)
            addFolderClickListenerForTagSearch(table, filteredFolders);

            tagSearchFrame.add(tagSearchPanel);
            tagSearchFrame.setVisible(true);
        }
    }

    // 태그 검색 결과 테이블의 클릭 리스너 추가
    private void addFolderClickListenerForTagSearch(JTable table, ArrayList<Folder> filteredFolders) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    Folder selectedFolder = filteredFolders.get(selectedRow);

                    // 독후감 정보 문자열 생성
                    String message = "도서명 : " + selectedFolder.getName() + "\n" +
                                     "저자명 : " + selectedFolder.getAuthorName() + "\n" +
                                     "별점 : " + getStars(selectedFolder.getRating()) + "\n" +
                                     "작성한 날짜 : " + selectedFolder.getModifiedDate() + "\n\n" +
                                     "내용 :\n" + selectedFolder.getReviewContent() + "\n\n" +
                                     "태그 : " + selectedFolder.getTagsAsString();

                    // 다이얼로그용 패널 생성
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());

                    // 텍스트 정보 추가
                    JTextArea textArea = new JTextArea(message);
                    textArea.setEditable(false);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(300, 200));
                    panel.add(scrollPane, BorderLayout.NORTH);

                    // 이미지가 있는 경우 추가
                    if (selectedFolder.getImagePath() != null && !selectedFolder.getImagePath().isEmpty()) {
                        File imageFile = new File(selectedFolder.getImagePath());
                        if (imageFile.exists()) {
                            ImageIcon icon = new ImageIcon(selectedFolder.getImagePath());
                            Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(img));
                            panel.add(imageLabel, BorderLayout.CENTER);
                        }
                    }

                    // 옵션 버튼 추가
                    int option = JOptionPane.showOptionDialog(
                            frame, panel, "독후감 내용",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"수정", "삭제", "닫기"},
                            "닫기"
                    );

                    if (option == 0) { // 수정 버튼 클릭
                        openEditForm(selectedFolder, selectedRow);
                    } else if (option == 1) { // 삭제 버튼 클릭
                        deleteFolder(selectedRow);
                    }
                }
            }
        });
    }
    
    public void createNewFolder(String folderName, String authorName, int rating, String reviewContent, String tags, String imagePath) {
        Date currentTime = new Date();
        Folder newFolder = new Folder(folderName, authorName, rating, currentTime, reviewContent, tags, imagePath);
        folders.add(newFolder);

        saveReviewToFile(newFolder);
        tableModel.fireTableDataChanged();
    }

    private void openReviewForm() {
        new ReviewForm(this);
    }

    private void sortFoldersByName() {
        Collections.sort(folders, Comparator.comparing(Folder::getName));
        tableModel.fireTableDataChanged();
    }

    private void sortFoldersByRecent() {
        Collections.sort(folders, Comparator.comparing(Folder::getModifiedDateAsDate).reversed());
        tableModel.fireTableDataChanged();
    }

    private void sortFoldersByOldest() {
        Collections.sort(folders, Comparator.comparing(Folder::getModifiedDateAsDate));
        tableModel.fireTableDataChanged();
    }

    private void sortFoldersByRating() {
        Collections.sort(folders, Comparator.comparing(Folder::getRating).reversed());
        tableModel.fireTableDataChanged();
    }

    private void refreshUI() {

        for (int i = 0; i < folders.size(); i++) {
            Folder folder = folders.get(i);
            JLabel folderLabel = new JLabel(folder.getName());
            folderLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            folderLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 커서 모양 변경 (클릭 가능하게)

            int folderIndex = i;
            folderLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showFolderDetails(folderIndex); // 클릭 시 해당 독후감 세부 내용을 보여줌
                }
            });

        }

        tableModel.fireTableDataChanged();
    }
    
    private void showFolderDetails(int index) {
        Folder selectedFolder = folders.get(index);

        String message = String.format(
                "도서명: %s\n저자명: %s\n별점: %s\n작성한 날짜: %s\n\n내용:\n%s\n\n태그: %s",
                selectedFolder.getName(),
                selectedFolder.getAuthorName(),
                getStars(selectedFolder.getRating()),
                selectedFolder.getModifiedDate(),
                selectedFolder.getReviewContent(),
                selectedFolder.getTagsAsString()
        );

        // 다이얼로그 생성
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION,
                null, new String[]{"수정", "삭제", "닫기"}, "닫기");

        // 이미지가 있는 경우, JLabel에 추가
        if (selectedFolder.getImagePath() != null && !selectedFolder.getImagePath().isEmpty()) {
            File imageFile = new File(selectedFolder.getImagePath());
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(selectedFolder.getImagePath());
                Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(img));
                optionPane.setMessage(new Object[]{message, imageLabel});
            }
        }

        JDialog dialog = optionPane.createDialog(frame, "독후감 내용");
        dialog.setVisible(true);

        int option = (int) optionPane.getValue();

        if (option == 0) { // 수정 버튼 클릭
            openEditForm(selectedFolder, index);
        } else if (option == 1) { // 삭제 버튼 클릭
            deleteFolder(index);
        }
    }


    private void addFolderClickListener(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    Folder selectedFolder = folders.get(selectedRow);

                    // 독후감 정보 문자열 생성
                    String message = "도서명 : " + selectedFolder.getName() + "\n" +
                                     "저자명 : " + selectedFolder.getAuthorName() + "\n" +
                                     "별점 : " + getStars(selectedFolder.getRating()) + "\n" +
                                     "작성한 날짜 : " + selectedFolder.getModifiedDate() + "\n\n" +
                                     "내용 :\n" + selectedFolder.getReviewContent() + "\n\n" +
                                     "태그 : " + selectedFolder.getTagsAsString();

                    // 다이얼로그용 패널 생성
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());

                    // 텍스트 정보 추가
                    JTextArea textArea = new JTextArea(message);
                    textArea.setEditable(false);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(300, 200));
                    panel.add(scrollPane, BorderLayout.NORTH);

                    // 이미지가 있는 경우 추가
                    if (selectedFolder.getImagePath() != null && !selectedFolder.getImagePath().isEmpty()) {
                        File imageFile = new File(selectedFolder.getImagePath());
                        if (imageFile.exists()) {
                            ImageIcon icon = new ImageIcon(selectedFolder.getImagePath());
                            Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(img));
                            panel.add(imageLabel, BorderLayout.CENTER);
                        }
                    }

                    // 옵션 버튼 추가
                    int option = JOptionPane.showOptionDialog(
                            frame, panel, "독후감 내용",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"수정", "삭제", "닫기"},
                            "닫기"
                    );

                    if (option == 0) { // 수정 버튼 클릭
                        openEditForm(selectedFolder, selectedRow);
                    } else if (option == 1) { // 삭제 버튼 클릭
                        deleteFolder(selectedRow);
                    }
                }
            }
        });
    }


    public void updateFolder(int index, Folder updatedFolder) {
        folders.set(index, updatedFolder); // 폴더 리스트 갱신
        tableModel.fireTableDataChanged(); // 테이블 갱신
        refreshUI(); // UI 갱신
        saveAllReviewsToFile(); // 파일 동기화
    }

    private void openEditForm(Folder folder, int folderIndex) {
        new ReviewEditForm(this, folder, folderIndex);
    }


    private void deleteFolder(int index) {
        folders.remove(index); // 메모리에서 삭제
        tableModel.fireTableDataChanged(); // 테이블 갱신
        refreshUI(); // UI 갱신
        saveAllReviewsToFile(); // 텍스트 파일 갱신
    }

    private void saveAllReviewsToFile() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) { // 덮어쓰기
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Folder folder : folders) {
                writer.write("도서명 : " + folder.getName() + "\n");
                writer.write("저자명 : " + folder.getAuthorName() + "\n");
                writer.write("별점 : " + folder.getRating() + "\n");
                writer.write("작성시간 : " + sdf.format(folder.getModifiedDateAsDate()) + "\n");
                writer.write("내용 : " + folder.getReviewContent() + "\n");
                writer.write("태그 : " + folder.getTagsAsString() + "\n");
                writer.write("이미지 : " + (folder.getImagePath() != null ? folder.getImagePath() : "없음") + "\n");
                writer.write("-------------------------------\n");
            }
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private String getStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        return stars.toString();
    }

    private void saveReviewToFile(Folder folder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write("도서명 : " + folder.getName() + "\n");
            writer.write("저자명 : " + folder.getAuthorName() + "\n");
            writer.write("별점 : " + folder.getRating() + "\n");
            writer.write("작성시간 : " + sdf.format(folder.getModifiedDateAsDate()) + "\n");
            writer.write("내용 : " + folder.getReviewContent() + "\n");
            writer.write("태그 : " + folder.getTagsAsString() + "\n");

            // 이미지 경로 저장
            if (folder.getImagePath() != null && !folder.getImagePath().isEmpty()) {
                writer.write("이미지 : " + folder.getImagePath() + "\n");
            } else {
                writer.write("이미지 : 없음\n");
            }

            writer.write("-------------------------------\n");
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReviewsFromFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String bookName = null, authorName = null, reviewContent = null, tags = null, imagePath = null;
            int rating = 0;
            Date modifiedDate = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("도서명 : ")) {
                    bookName = line.substring(6).trim();
                } else if (line.startsWith("저자명 : ")) {
                    authorName = line.substring(6).trim();
                } else if (line.startsWith("별점 : ")) {
                    rating = Integer.parseInt(line.substring(5).trim());
                } else if (line.startsWith("작성시간 : ")) {
                    String dateString = line.substring(6).trim();
                    modifiedDate = sdf.parse(dateString);
                } else if (line.startsWith("내용 : ")) {
                    reviewContent = line.substring(5).trim();
                } else if (line.startsWith("태그 : ")) {
                    tags = line.substring(5).trim();
                } else if (line.startsWith("이미지 : ")) {
                    imagePath = line.substring(6).trim();
                    if (imagePath.equals("없음")) {
                        imagePath = null;
                    }
                } else if (line.startsWith("-------------------------------")) {
                    if (bookName != null && authorName != null && modifiedDate != null) {
                        folders.add(new Folder(bookName, authorName, rating, modifiedDate, reviewContent, tags, imagePath));
                    }
                    bookName = null;
                    authorName = null;
                    reviewContent = null;
                    tags = null;
                    imagePath = null;
                    rating = 0;
                    modifiedDate = null;
                }
            }
            refreshUI();
        } catch (IOException e) {
            System.out.println("파일 읽기 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("날짜 형식 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("파일 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Books_zip();
    }

    class FolderTableModel extends AbstractTableModel {
        private ArrayList<Folder> folders;
        private String[] columnNames = { "도서명", "별점", "작성한 날짜" };

        public FolderTableModel(ArrayList<Folder> folders) {
            this.folders = folders;
        }

        @Override
        public int getRowCount() {
            return folders.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Folder folder = folders.get(rowIndex);
            switch (columnIndex) {
                case 0: return folder.getName();
                case 1: return getStars(folder.getRating());
                case 2: return folder.getModifiedDate();
                default: return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }
}