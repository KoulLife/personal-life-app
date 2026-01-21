package koul.PersonalApp.alert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelType {
    SLACK("슬랙"),
    TELEGRAM("텔레그램"),
    EMAIL("이메일");

    private final String description;
}
