package finalterm;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TagSearchGUI extends JFrame {
    private TagSearch tagSearch; // 백엔드 로직
    private DefaultListModel<String> tagListModel; // 태그 목록 모델
    private JList<String> tagList; // 태그 목록 UI
    private JTextArea resultArea; // 결과 출력 영역

    public TagSearchGUI() {
        // 백엔드 초기화
        tagSearch = new TagSearch();

        // 프레임 설정
        setTitle("태그 검색 및 추가 시스템");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 태그 목록 패널
        JPanel tagPanel = new JPanel(new BorderLayout());
        JLabel tagLabel = new JLabel("태그 목록:");
        tagListModel = new DefaultListModel<>();
        tagList = new JList<>(tagListModel);
        tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 다중 선택 가능
        JScrollPane tagScrollPane = new JScrollPane(tagList);

        // 결과 출력 패널
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // 태그 패널 구성
        tagPanel.add(tagLabel, BorderLayout.NORTH);
        tagPanel.add(tagScrollPane, BorderLayout.CENTER);

        // 메인 프레임 구성
        add(tagPanel, BorderLayout.WEST);
        add(resultScrollPane, BorderLayout.CENTER);

        // 예시 데이터 추가
        initializeExampleData();

        // JList 선택 이벤트 처리 (즉시 검색)
        tagList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // 이벤트가 최종적으로 완료되었을 때만 처리
                    List<String> selectedTags = tagList.getSelectedValuesList();
                    if (selectedTags.isEmpty()) {
                        resultArea.setText("태그를 선택하세요.");
                        return;
                    }

                    Set<String> selectedTagSet = new HashSet<>(selectedTags);
                    List<Map.Entry<String, Integer>> sortedResults = tagSearch.searchByMultipleTagsAndSort(selectedTagSet);

                    // 결과 출력
                    StringBuilder resultText = new StringBuilder("선택된 태그: " + selectedTags + "\n\n");
                    resultText.append("[태그가 겹치는 학생 정렬 결과]\n");
                    for (Map.Entry<String, Integer> entry : sortedResults) {
                        String studentNum = entry.getKey();
                        int matchingCount = entry.getValue();

                        // 학생이 가진 태그와 선택된 태그의 교집합 계산
                        Set<String> studentTags = tagSearch.getTagsByStudent(studentNum);
                        Set<String> commonTags = new HashSet<>(studentTags);
                        commonTags.retainAll(selectedTagSet); // 교집합 계산

                        resultText.append("학생번호: ").append(studentNum)
                                .append(", 겹치는 태그 수: ").append(matchingCount)
                                .append(", 겹치는 태그: ").append(commonTags).append("\n");
                    }
                    resultArea.setText(resultText.toString());
                }
            }
        });
    }

    /**
     * 예시 데이터를 초기화하는 메서드
     */
    private void initializeExampleData() {
        // 예시 태그 추가 (실제 데이터는 CSV 로드 또는 동적 추가 가능)
        tagListModel.addElement("축구");
        tagListModel.addElement("농구");
        tagListModel.addElement("수영");
        tagListModel.addElement("야구");
        tagListModel.addElement("오버워치");

        // 예시 학생 및 태그 추가
        tagSearch.addStudent("20230001");
        tagSearch.addStudent("20230002");

        tagSearch.addTagToStudent("20230001", "축구");
        tagSearch.addTagToStudent("20230001", "농구");

        tagSearch.addTagToStudent("20230002", "수영");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagSearchGUI gui = new TagSearchGUI();
            gui.setVisible(true);
        });
    }
}
