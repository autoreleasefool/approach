import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Game.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Game.title : Strings.Game.List.title
	}
}
