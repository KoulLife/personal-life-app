package koul.PersonalApp.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import koul.PersonalApp.ai.tool.ProjectGroupTool;
import koul.PersonalApp.ai.tool.ProjectPlanningTool;

@RestController
@RequestMapping("/ai")
public class AiChatController {

    private final ChatClient chatClient;

    public AiChatController(ChatClient.Builder chatClientBuilder,
            ProjectGroupTool projectGroupTool,
            ProjectPlanningTool projectPlanningTool) {
        this.chatClient = chatClientBuilder
                .defaultTools(projectGroupTool, projectPlanningTool) // 도구 등록
                .defaultSystem("당신은 사용자의 목표 달성을 돕는 '프로젝트 플래닝 AI 에이전트'입니다.\n" +
                        "사용자가 '로드맵 짜줘', '공부 계획 세워줘'와 같이 모호하게 요청하더라도, 당신은 스스로 단계를 구상하여 즉시 실행해야 합니다.\n" +
                        "\n" +
                        "**반드시 지켜야 할 절차:**\n" +
                        "1. 사용자의 요청에 맞는 '프로젝트 그룹 이름'을 스스로 정해서 `createProjectGroup` 도구를 호출하세요. (반환된 Group ID 확보)\n" +
                        "2. 목표를 달성하기 위한 3~5단계의 순차적인 세부 계획을 스스로 생성하세요.\n" +
                        "3. 확보한 Group ID와 생성한 세부 계획 리스트를 사용하여 `createLinkedProjects` 도구를 호출하세요.\n" +
                        "4. 모든 작업이 완료되면 사용자에게 어떤 그룹과 프로젝트들이 생성되었는지 요약해서 알려주세요.\n" +
                        "\n" +
                        "**주의사항:**\n" +
                        "- 사용자에게 '어떤 단계로 할까요?'라고 되묻지 마세요. 당신이 전문가로서 제안하고 바로 DB에 저장하세요.\n" +
                        "- 도구 호출 권한이 있으므로 적극적으로 사용하세요.")
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "message") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
