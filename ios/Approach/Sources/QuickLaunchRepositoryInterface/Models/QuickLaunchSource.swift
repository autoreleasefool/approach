import ModelsLibrary

public struct QuickLaunchSource: Equatable, Codable {
	public let bowler: Bowler.Summary
	public let league: League.SeriesHost
}
