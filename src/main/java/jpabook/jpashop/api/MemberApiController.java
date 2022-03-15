package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 v1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *  - 엔티티에 API 검증을 위한 로직이 들어간다(@NotEmpty 등)
     *  - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를
     *    위한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     *
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     *  - 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
     *  - 엔티티와 API 스펙을 명확하게 분리할 수 있다.
     *  - 엔티티가 변해도 API 스펙이 변하지 않는다.
     * */

    /** 등록 API*/
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }

    /** 수정 API
     * PUT은 전체 업데이트를 할 때 사용하는 것이 적합하다.
     * 부분 업데이트를 하려면 PATCH 또는 POST를 사용하는 것이 REST 스타일에 맞다.
     * */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request ){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    /** 조회 v1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     *  - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *      - 기본적으로 엔티티의 모든 값이 노출된다.
     *      - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등..)
     *      - 추가 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다. (별도의 RESULT 클래스 생성으로 해결)
     *
     * 결론
     * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
     *
     * 참고
     * - 엔티티를 외부에 노출하지 말자!(강조*100)
     * */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findeMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){

        List<Member> findMembers = memberService.findeMembers();
        //엔티티 DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size());
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
