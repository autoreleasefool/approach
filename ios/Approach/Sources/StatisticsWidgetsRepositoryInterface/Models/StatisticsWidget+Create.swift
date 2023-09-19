import Foundation
import ModelsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: StatisticsWidget.ID
		public var created: Date
		public var bowlerId: Bowler.ID?
		public var leagueId: League.ID?
		public var timeline: StatisticsWidget.Timeline
		public var statistic: String
		public var context: String
		public var priority: Int

		public var source: StatisticsWidget.Source? {
			get {
				if let bowlerId {
					return .bowler(bowlerId)
				} else if let leagueId {
					return .league(leagueId)
				} else {
					return nil
				}
			}
			set {
				switch newValue {
				case .none:
					self.bowlerId = nil
					self.leagueId = nil
				case let .bowler(bowlerId):
					self.bowlerId = bowlerId
					self.leagueId = nil
				case let .league(leagueId):
					self.bowlerId = nil
					self.leagueId = leagueId
				}
			}
		}
	}
}

extension StatisticsWidget.Configuration {
	public func make(on created: Date, context: String, priority: Int) -> StatisticsWidget.Create? {
		guard let source else { return nil }
		return .init(
			id: id,
			created: created,
			bowlerId: source.bowlerId,
			leagueId: source.leagueId,
			timeline: timeline,
			statistic: statistic,
			context: context,
			priority: priority
		)
	}
}
