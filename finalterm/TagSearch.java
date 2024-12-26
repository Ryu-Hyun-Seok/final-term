package finalterm;

import java.util.*;

/**
 * TagSearch 클래스는 학생과 태그를 관리하고, 태그를 기반으로 학생을 검색하는 기능을 제공합니다.
 *
 * 주요 기능:
 * - 학생 추가
 * - 학생에게 태그 추가
 * - 태그 기반 검색
 * - 태그 및 학생 정보 조회
 */
public class TagSearch {
    /**
     * 태그와 연결된 학번 목록을 저장하는 맵.
     * 키: 태그(String), 값: 해당 태그와 연결된 학번(Set<String>)
     */
    private Map<String, Set<String>> tagMap;

    /**
     * 학번과 학생 객체(Student)를 저장하는 맵.
     * 키: 학번(String), 값: 학생 객체(Student)
     */
    private Map<String, Student> studentMap;

    /**
     * TagSearch 생성자.
     * 태그 맵(tagMap)과 학생 맵(studentMap)을 초기화합니다.
     */
    public TagSearch() {
        this.tagMap = new HashMap<>();
        this.studentMap = new HashMap<>();
    }

    /**
     * 새로운 학생을 추가합니다.
     *
     * @param studentNum 추가할 학생의 학번
     */
    public void addStudent(String studentNum) {
        if (!studentMap.containsKey(studentNum)) {
            studentMap.put(studentNum, new Student(studentNum));
            System.out.println("학생 추가 성공: " + studentNum);
        } else {
            System.out.println("이미 존재하는 학생입니다: " + studentNum);
        }
    }

    /**
     * 특정 학생에게 태그를 추가하고, 태그-학번 관계를 업데이트합니다.
     *
     * @param studentNum 태그를 추가할 학생의 학번
     * @param tag        추가할 태그
     */
    public void addTagToStudent(String studentNum, String tag) {
        if (!studentMap.containsKey(studentNum)) {
            System.out.println("학생이 존재하지 않습니다: " + studentNum);
            return;
        }

        // 학생 객체 가져오기
        Student student = studentMap.get(studentNum);

        // 학생에게 태그 추가
        if (student.addTag(tag)) {
            // 태그-학번 관계 업데이트
            tagMap.putIfAbsent(tag, new HashSet<>());
            tagMap.get(tag).add(studentNum);
            System.out.println("태그 추가 성공: " + tag + " -> " + studentNum);
        } else {
            System.out.println("이미 존재하는 태그입니다: " + tag);
        }
    }

    /**
     * 여러 태그로 검색하여 겹치는 태그 수가 많은 학생을 내림차순으로 정렬합니다.
     *
     * @param tags 검색할 태그 목록(Set<String>)
     * @return 겹치는 태그 수를 기준으로 정렬된 결과 목록 (학생 번호와 겹치는 태그 수의 리스트)
     */
    public List<Map.Entry<String, Integer>> searchByMultipleTagsAndSort(Set<String> tags) {
        Map<String, Integer> matchingStudents = new HashMap<>();

        // 각 학생의 겹치는 태그 수 계산
        for (String tag : tags) {
            Set<String> studentsWithTag = tagMap.getOrDefault(tag, Collections.emptySet());
            for (String student : studentsWithTag) {
                matchingStudents.put(student, matchingStudents.getOrDefault(student, 0) + 1);
            }
        }

        // 결과를 내림차순으로 정렬
        List<Map.Entry<String, Integer>> sortedStudents = new ArrayList<>(matchingStudents.entrySet());
        sortedStudents.sort((a, b) -> b.getValue().compareTo(a.getValue())); // 값(겹치는 태그 수)에 따라 내림차순 정렬

        return sortedStudents;
    }

    /**
     * 특정 학생이 등록한 모든 태그를 반환합니다.
     *
     * @param studentNum 조회할 학생의 학번
     * @return 해당 학생이 등록한 모든 태그(Set<String>)
     */
    public Set<String> getTagsByStudent(String studentNum) {
        if (!studentMap.containsKey(studentNum)) {
            System.out.println("학생이 존재하지 않습니다: " + studentNum);
            return Collections.emptySet();
        }
        return studentMap.get(studentNum).getTags();
    }

    /**
     * 현재 등록된 모든 태그를 반환합니다.
     *
     * @return 등록된 모든 태그(Set<String>)
     */
    public Set<String> getAllTags() {
        return tagMap.keySet();
    }
}
