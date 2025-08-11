import ComposableArchitecture
import StringsLibrary

extension AlertState where Action == GamesEditorNext.Destination.DuplicateLanesAlertAction {
	static var duplicateLanes: Self {
		Self {
			TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.title)
		} actions: {
			ButtonState(action: .confirmDuplicateLanes) {
				TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.copyToAll)
			}

			ButtonState(role: .cancel, action: .didTapDismissButton) {
				TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.dismiss)
			}
		} message: {
			TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.message)
		}
	}
}
