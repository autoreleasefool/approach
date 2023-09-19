import AssetsLibrary
import DateTimeLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
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
			filterLabel(Strings.Statistics.Filter.Label.League.title, value: league.name, type: .league)
		} else if let recurrence = filter.leagueFilter.recurrence {
			filterLabel(Strings.Statistics.Filter.Label.League.repeats, value: String(describing: recurrence), type: .league)
		}
	}

	@ViewBuilder private var seriesFilters: some View {
		if let series = sources.series {
			filterLabel(Strings.Statistics.Filter.Label.Series.title, value: series.date.mediumFormat, type: .series)
		} else {
			if let startDate = filter.seriesFilter.startDate {
				filterLabel(Strings.Statistics.Filter.Label.Series.starts, value: startDate.longFormat, type: .series)
			}

			if let endDate = filter.seriesFilter.endDate {
				filterLabel(Strings.Statistics.Filter.Label.Series.ends, value: endDate.longFormat, type: .series)
			}

			if let alley = filter.seriesFilter.alley {
				switch alley {
				case let .alley(alley):
					filterLabel(Strings.Statistics.Filter.Label.Series.Alley.title, value: alley.name, type: .alley)
				case let .properties(properties):
					if let material = properties.material {
						filterLabel(
							Strings.Statistics.Filter.Label.Series.Alley.material,
							value: String(describing: material),
							type: .alley
						)
					}
					if let mechanism = properties.mechanism {
						filterLabel(
							Strings.Statistics.Filter.Label.Series.Alley.mechanism,
							value: String(describing: mechanism),
							type: .alley
						)
					}
					if let pinFall = properties.pinFall {
						filterLabel(
							Strings.Statistics.Filter.Label.Series.Alley.pinFall,
							value: String(describing: pinFall),
							type: .alley
						)
					}
					if let pinBase = properties.pinBase {
						filterLabel(
							Strings.Statistics.Filter.Label.Series.Alley.pinBase,
							value: String(describing: pinBase),
							type: .alley
						)
					}
				}
			}
		}
		EmptyView()
	}

	@ViewBuilder private var gameFilters: some View {
		if let game = sources.game {
			filterLabel(Strings.Statistics.Filter.Label.Game.title, value: String(game.index), type: .matchPlay)
		} else {
			if let lanesFilter = filter.gameFilter.lanes {
				switch lanesFilter {
				case let .lanes(lanes):
					filterLabel(
						Strings.Statistics.Filter.Label.Game.Lanes.title,
						value: lanes.map(\.label).joined(separator: ", "),
						type: .alley
					)
				case let .positions(positions):
					filterLabel(
						Strings.Statistics.Filter.Label.Game.Lanes.positions,
						value: positions.map { String(describing: $0) }.joined(separator: ", "),
						type: .alley
					)
				}
			}

			if !filter.gameFilter.gearUsed.isEmpty {
				filterLabel(
					Strings.Statistics.Filter.Label.Gear.title,
					value: filter.gameFilter.gearUsed.map(\.name).joined(separator: ", "),
					type: .gear
				)
			}

			if let opponent = filter.gameFilter.opponent {
				filterLabel(Strings.Statistics.Filter.Label.Opponent.title, value: opponent.name, type: .matchPlay)
			}
		}
	}

	@ViewBuilder private var frameFilters: some View {
		if !filter.frameFilter.bowlingBallsUsed.isEmpty {
			filterLabel(
				Strings.Statistics.Filter.Label.Gear.ballsRolled,
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
			Text(value)
				.font(size.valueFont)
		}
		.foregroundColor(type.text)
		.padding(size.padding)
		.background(type.background)
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
	case series
	case alley
	case gear
	case matchPlay

	var background: ColorAsset {
		switch self {
		case .league: return Asset.Colors.Filters.league
		case .series: return Asset.Colors.Filters.series
		case .alley: return Asset.Colors.Filters.alley
		case .gear: return Asset.Colors.Filters.gear
		case .matchPlay: return Asset.Colors.Filters.matchPlay
		}
	}

	var text: ColorAsset {
		switch self {
		case .league: return Asset.Colors.Filters.Text.onLeague
		case .series: return Asset.Colors.Filters.Text.onSeries
		case .alley: return Asset.Colors.Filters.Text.onAlley
		case .gear: return Asset.Colors.Filters.Text.onGear
		case .matchPlay: return Asset.Colors.Filters.Text.onMatchPlay
		}
	}
}
