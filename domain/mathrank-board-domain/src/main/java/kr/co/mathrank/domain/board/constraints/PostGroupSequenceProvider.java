package kr.co.mathrank.domain.board.constraints;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import kr.co.mathrank.domain.board.dto.PostRegisterCommand;

public class PostGroupSequenceProvider implements DefaultGroupSequenceProvider<PostRegisterCommand> {
	@Override
	public List<Class<?>> getValidationGroups(PostRegisterCommand postRegisterCommand) {
		final List<Class<?>> groups = new ArrayList<>();
		groups.add(PostRegisterCommand.class);

		// 초기 호출 방지
		if (postRegisterCommand == null || postRegisterCommand.postType() == null) {
			return groups;
		}

		switch (postRegisterCommand.postType()) {
			case SINGLE_PROBLEM -> groups.add(ValidationGroups.SingleProblemPostGroup.class);
			case ASSESSMENT -> groups.add(ValidationGroups.AssessmentPostGroup.class);
			case CONTEST -> groups.add(ValidationGroups.ContestPostGroup.class);
			case FREE -> groups.add(ValidationGroups.FreePostGroup.class);
			case NOTICE -> groups.add(ValidationGroups.NoticePostGroup.class);
		}
		return groups;
	}
}
