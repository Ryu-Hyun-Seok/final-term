package finalterm;


import java.util.HashSet;
import java.util.Set;

public class Student {
    private HashSet<String> tags;
    private String studentNumber;


    public Student(String studentNum) {
        this.studentNumber = studentNumber;
        this.tags = new HashSet<>();
    }

    // 태그 추가 메서드
    public boolean addTag(String tag) {
        return tags.add(tag); // 중복 방지
    }

    // 태그 삭제 메서드
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    // 태그 목록 반환 메서드
    public Set<String> getTags() {
        return tags;
    }

    // 학생 번호 반환 메서드
    public String getStudentNumber() {
        return studentNumber;
    }
}
