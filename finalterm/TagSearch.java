package finalterm;

import java.util.*;

public class TagSearch {
    /**
     * 키: 태그목록, 밸류: 각 태그와 연결된 학번목록
     */
    private  Map<String, Set<String>> tagMap;

    /**
     * 키: 학번, 값: 해당 학생 객체(Student)
     */
    private Map<String, Student> studentMap;

    public TagSearch() {
        this.tagMap = new HashMap<>();
        this.studentMap = new HashMap<>();
    }

    /**
     * 새로운 학생 추가
     * @param studentNum 학번
     */
    public void addStudent(String studentNum){
        if (!studentMap.containsKey(studentNum)) {
            studentMap.put(studentNum, new Student(studentNum));
            System.out.println("학생 추가 성공: " + studentNum);
        } else {
            System.out.println("이미 존재하는 학생입니다: " + studentNum);
        }
    }

    /**
     * 학생에게 태그를 추가하고, 태그-학번 관계 업데이트
     * @param studentNum 학번
     * @param tag 추가할 태그
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
     * 특정 태그에 연결된 모든 학번 조회
     * @param tag 검색할 태그
     * @return 해당 태그에 연결된 학번 목록(Set)
     */
    public Set<String> searchByTag(String tag) {
        return tagMap.getOrDefault(tag, Collections.emptySet());
    }

    /**
     * 특정 학생이 등록한 모든 태그 조회
     * @param studentNum 검색할 학생 번호
     * @return 해당 학생이 등록한 태그 목록(Set)
     */

    /**
     * 여러 태그로 검색하여 겹치는 태그 수가 많은 학생을 내림차순으로 정렬
     * @param tags 검색할 태그 목록(Set)
     * @return 정렬된 결과 (학생번호와 겹치는 태그 수의 리스트)
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
    public Set<String> getTagsByStudent(String studentNum) {
        if (!studentMap.containsKey(studentNum)) {
            System.out.println("학생이 존재하지 않습니다: " + studentNum);
            return Collections.emptySet();
        }
        return studentMap.get(studentNum).getTags();
    }

    public static void main(String[] args) {
        TagSearch tagSearch = new TagSearch();

        // 학생 추가
        tagSearch.addStudent("20230001");
        tagSearch.addStudent("20230002");
        tagSearch.addStudent("20230003");

        // 태그 추가
        tagSearch.addTagToStudent("20230001", "축구");
        tagSearch.addTagToStudent("20230001", "오버워치");
        tagSearch.addTagToStudent("20230001", "농구");
        tagSearch.addTagToStudent("20230001", "수영");

        tagSearch.addTagToStudent("20230002", "수영");
        tagSearch.addTagToStudent("20230002", "농구");

        tagSearch.addTagToStudent("20230003", "축구");
        tagSearch.addTagToStudent("20230003", "야구");

        // 특정 태그에 연결된 학번 조회
        System.out.println("축구 태그에 연결된 학번: " + tagSearch.searchByTag("축구"));

        // 특정 학생이 등록한 모든 태그 조회
        System.out.println("20230001의 등록된 태그: " + tagSearch.getTagsByStudent("20230001"));
        System.out.println("20230002의 등록된 태그: " + tagSearch.getTagsByStudent("20230002"));

        // 여러 태그로 검색하여 정렬된 결과 출력
        Set<String> searchTags = new HashSet<>(Arrays.asList("축구", "수영", "농구"));
        List<Map.Entry<String, Integer>> sortedResults = tagSearch.searchByMultipleTagsAndSort(searchTags);

        System.out.println("\n[태그가 겹치는 학생 정렬 결과]");
        for (Map.Entry<String, Integer> entry : sortedResults) {
            System.out.println("학생번호: " + entry.getKey() + ", 겹치는 태그 수: " + entry.getValue());
        }
    }
}

