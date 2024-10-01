import ModelsLibrary

public struct QuickLaunchSource: Equatable, Codable, Sendable {
	public let bowler: Bowler.Summary
	public let league: League.SeriesHost
}
