import Foundation

public struct SeriesLane: Sendable, Hashable, Codable {
	public let seriesId: Series.ID
	public let laneId: Lane.ID

	public init(
		seriesId: Series.ID,
		laneId: Lane.ID
	) {
		self.seriesId = seriesId
		self.laneId = laneId
	}
}
