package finalterm;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * TagSearchGUI 클래스는 태그 검색 및 추가 시스템의 GUI를 구현합니다.
 * 태그 목록을 왼쪽에 표시하고, 선택된 태그와 관련된 학생 정보를 오른쪽에 출력합니다.
 *
 * 주요 기능:
 * - 태그 목록 표시
 * - 태그 기반 학생 검색
 * - CSV 파일로 초기 데이터 로드
 */
public class TagSearchGUI extends JFrame {
    private TagSearch tagSearch; // 백엔드 로직
    private DefaultListModel<String> tagListModel; // 태그 목록 모델
    private JList<String> tagList; // 태그 목록 UI
    private JTextArea resultArea; // 결과 출력 영역

    /**
     * TagSearchGUI 생성자.
     * GUI를 초기화하고 CSV 파일에서 데이터를 로드합니다.
     */
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
        loadInitialDataFromCSV("initial_data.csv");

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
     * 태그 목록을 초기화하는 메서드.
     * 현재 등록된 모든 태그를 가져와 JList에 추가합니다.
     */
    private void initializeTagList() {
        // 기존 태그 데이터를 가져와 JList에 추가
        tagListModel.clear();
        Set<String> allTags = tagSearch.getAllTags();
        for (String tag : allTags) {
            tagListModel.addElement(tag);
        }
    }

    /**
     * 태그 목록을 갱신하는 메서드.
     * 새로운 태그가 추가되거나 삭제될 때 호출됩니다.
     */
    private void refreshTagList() {
        tagListModel.clear();
        Set<String> allTags = tagSearch.getAllTags();
        for (String tag : allTags) {
            tagListModel.addElement(tag);
        }
    }

    /**
     * CSV 파일에서 초기 데이터를 로드하는 메서드.
     * 파일의 각 행은 학생 번호와 해당 학생의 태그로 구성됩니다.
     *
     * @param filename CSV 파일 경로
     */
    private void loadInitialDataFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                String studentNum = values[0];
                tagSearch.addStudent(studentNum);
                for (int i = 1; i < values.length; i++) {
                    if (!values[i].isEmpty()) {
                        tagSearch.addTagToStudent(studentNum, values[i]);
                    }
                }
            }
            initializeTagList(); // CSV 데이터 로드 후 태그 목록 초기화
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "CSV 파일 로드 실패: " + e.getMessage());
        }
    }

    /**
     * 프로그램 실행 진입점.
     * SwingUtilities.invokeLater를 사용하여 GUI를 생성하고 표시합니다.
     *
     * @param args 명령줄 인수 (사용되지 않음)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagSearchGUI gui = new TagSearchGUI();
            gui.setVisible(true);
        });
    }
}
