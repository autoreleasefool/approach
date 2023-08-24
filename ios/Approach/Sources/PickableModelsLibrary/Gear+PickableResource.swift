import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Gear.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Gear.title : Strings.Gear.List.title
	}
}
