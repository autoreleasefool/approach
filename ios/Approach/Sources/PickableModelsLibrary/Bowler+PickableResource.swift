import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Bowler.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}
