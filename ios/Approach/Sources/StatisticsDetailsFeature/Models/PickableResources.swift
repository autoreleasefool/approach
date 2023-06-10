import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

extension League.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.League.title : Strings.League.List.title
	}
}

extension Series.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Series.title : Strings.Series.List.title
	}
}

extension Game.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Game.title : Strings.Game.List.title
	}
}
