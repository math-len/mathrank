package kr.co.mathrank.domain.contents.constraints;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import kr.co.mathrank.domain.contents.dto.ContentRegisterCommand;

public class ContentValidationGroupProvider implements DefaultGroupSequenceProvider<ContentRegisterCommand> {
	@Override
	public List<Class<?>> getValidationGroups(ContentRegisterCommand command) {
		final List<Class<?>> groups = new ArrayList<>();
		groups.add(ContentRegisterCommand.class);

		if (command == null || command.contentType() == null) {
			return groups;
		}

		switch (command.contentType()) {
			case VIDEO -> groups.add(ValidationGroup.VideoContentConstraints.class);
			case WORKBOOK -> groups.add(ValidationGroup.WorkBookContentConstraints.class);
			case NAESIN_WORKBOOK -> groups.add(ValidationGroup.NaeSinWorkBookContentConstraints.class);
		}

		return groups;
	}
}
