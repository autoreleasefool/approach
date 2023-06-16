import AssetsLibrary
import DateTimeLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import SwiftUI
import ViewsLibrary

public struct StatisticsFilterView: View {
	let sources: TrackableFilter.Sources
	let filter: TrackableFilter
	let size: Size

	public var body: some View {
		ScrollView(.horizontal, showsIndicators: false) {
			HStack(alignment: .top, spacing: .smallSpacing) {
				Color.clear
					.frame(width: .smallSpacing)

				leagueFilters
				seriesFilters
				gameFilters
				frameFilters

				Color.clear
					.frame(width: .smallSpacing)
			}
		}
	}

	@ViewBuilder private var leagueFilters: some View {
		if let league = sources.league {
			filterLabel("League", value: league.name, type: .league)
		} else if let recurrence = filter.leagueFilter.recurrence {
			filterLabel("Repeats?", value: String(describing: recurrence), type: .league)
		}
	}

	@ViewBuilder private var seriesFilters: some View {
		if let series = sources.series {
			filterLabel("Series", value: series.date.mediumFormat, type: .seriesDate)
		} else {
			if let startDate = filter.seriesFilter.startDate {
				filterLabel("Starts", value: startDate.mediumFormat, type: .seriesDate)
			}

			if let endDate = filter.seriesFilter.endDate {
				filterLabel("Ends", value: endDate.mediumFormat, type: .seriesDate)
			}

			if let alley = filter.seriesFilter.alley {
				switch alley {
				case let .alley(alley):
					filterLabel("Alley", value: alley.name, type: .alley)
				case let .properties(properties):
					if let material = properties.material {
						filterLabel("Material", value: String(describing: material), type: .alley)
					}
					if let mechanism = properties.mechanism {
						filterLabel("Mechanism", value: String(describing: mechanism), type: .alley)
					}
					if let pinFall = properties.pinFall {
						filterLabel("Pin Fall", value: String(describing: pinFall), type: .alley)
					}
					if let pinBase = properties.pinBase {
						filterLabel("Pin Base", value: String(describing: pinBase), type: .alley)
					}
				}
			}
		}
		EmptyView()
	}

	@ViewBuilder private var gameFilters: some View {
		if let game = sources.game {
			filterLabel("Game", value: String(game.index), type: .matchPlay)
		} else {
			if let lanesFilter = filter.gameFilter.lanes {
				switch lanesFilter {
				case let .lanes(lanes):
					filterLabel("Lanes", value: lanes.map(\.label).joined(separator: ", "), type: .alley)
				case let .positions(positions):
					filterLabel(
						"Lane Positions",
						value: positions.map { String(describing: $0) }.joined(separator: ", "),
						type: .alley
					)
				}
			}

			if !filter.gameFilter.gearUsed.isEmpty {
				filterLabel("Gear", value: filter.gameFilter.gearUsed.map(\.name).joined(separator: ", "), type: .gear)
			}

			if let opponent = filter.gameFilter.opponent {
				filterLabel("Opponent", value: opponent.name, type: .matchPlay)
			}
		}
	}

	@ViewBuilder private var frameFilters: some View {
		if !filter.frameFilter.bowlingBallsUsed.isEmpty {
			filterLabel(
				"Balls Rolled",
				value: filter.frameFilter.bowlingBallsUsed.map(\.name).joined(separator: ", "),
				type: .gear
			)
		}
	}

	private func filterLabel(_ title: String, value: String, type: StatisticsFilterType) -> some View {
		VStack(alignment: .leading, spacing: .tinySpacing) {
			Text(title)
				.font(size.titleFont)
				.fontWeight(size.titleFontWeight)
				.foregroundColor(type.textColor)
			Text(value)
				.font(size.valueFont)
				.foregroundColor(type.textColor)
		}
		.padding(size.padding)
		.background(type.backgroundColor)
		.cornerRadius(.standardRadius)
	}
}

extension StatisticsFilterView {
	public enum Size {
		case compact
		case regular

		var titleFont: Font {
			switch self {
			case .compact: return .subheadline
			case .regular: return .headline
			}
		}

		var valueFont: Font {
			switch self {
			case .compact: return .caption
			case .regular: return .subheadline
			}
		}

		var titleFontWeight: Font.Weight? {
			switch self {
			case .compact: return nil
			case .regular: return .medium
			}
		}

		var padding: CGFloat {
			switch self {
			case .compact: return .unitSpacing
			case .regular: return .smallSpacing
			}
		}
	}
}

enum StatisticsFilterType {
	case league
	case seriesDate
	case alley
	case gear
	case matchPlay

	var backgroundColor: Color {
		switch self {
		case .league: return .appFiltersSeaGreen
		case .seriesDate: return .appFiltersPuce
		case .alley: return .appFiltersEnglishViolet
		case .gear: return .appFiltersCeladon
		case .matchPlay: return .appFiltersAuburn
		}
	}

	var textColor: Color {
		switch self {
		case .league: return .white
		case .seriesDate: return .white
		case .alley: return .white
		case .gear: return .black
		case .matchPlay: return .white
		}
	}
}
