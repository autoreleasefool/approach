import SharedModelsLibrary

extension Frame {
	func allRolls() -> [Roll?] {
		rolls + Array(repeating: nil, count: 3 - rolls.count)
	}
}
