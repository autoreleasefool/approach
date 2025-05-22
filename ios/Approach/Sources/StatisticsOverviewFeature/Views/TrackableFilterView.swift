import AssetsLibrary
import DateTimeLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct TrackableFilterView: View {
	let filter: TrackableFilter
	let sources: TrackableFilter.Sources
	let configuration: Configuration

	init(filter: TrackableFilter, sources: TrackableFilter.Sources) {
		self.filter = filter
		self.sources = sources
		self.configuration = filter.source.configuration
	}

	public var body: some View {
		HStack(alignment: sources.subtitles.isEmpty ? .center : .top, spacing: 0) {
			Image(systemName: configuration.primarySystemImage)
				.resizable()
				.scaledToFit()
				.frame(width: .extraTinyIcon, height: .extraTinyIcon)
				.padding(.smallSpacing)
				.background(
					Circle()
						.stroke(.black.opacity(0.2))
				)

			VStack(alignment: .leading) {
				Text(sources.primaryTitle)
					.font(.headline)

				ForEach(sources.subtitles, id: \.self) {
					Text($0)
						.font(.caption)
				}
			}
			.padding(.horizontal, .standardSpacing)

			Spacer()

			VStack(alignment: .trailing) {
				if let from = filter.seriesFilter.startDate,
					 let until = filter.seriesFilter.endDate {
					BadgeView("\(from.shortFormat) — \(until.shortFormat)", style: .primary)
				} else if let from = filter.seriesFilter.startDate {
					BadgeView("Starting \(from.shortFormat)", style: .primary)
				} else if let until = filter.seriesFilter.endDate {
					BadgeView("Ending \(until.shortFormat)", style: .primary)
				}
			}
			.font(.caption)
		}
		.padding(.standardSpacing)
		.foregroundColor(configuration.foreground)
		.background(configuration.background)
	}
}

extension TrackableFilterView {
	struct Configuration {
		let primarySystemImage: String
		let background: Color
		let foreground: Color
	}
}

extension TrackableFilter.Source {
	var configuration: TrackableFilterView.Configuration {
		switch self {
		case .bowler:
			TrackableFilterView.Configuration(
				primarySystemImage: "person.fill",
				background: Asset.Colors.TrackableFilters.bowler.swiftUIColor,
				foreground: Asset.Colors.TrackableFilters.Text.onBowler.swiftUIColor
			)
		case .league:
			TrackableFilterView.Configuration(
				primarySystemImage: "list.bullet",
				background: Asset.Colors.TrackableFilters.league.swiftUIColor,
				foreground: Asset.Colors.TrackableFilters.Text.onLeague.swiftUIColor
			)
		case .series:
			TrackableFilterView.Configuration(
				primarySystemImage: "calendar",
				background: Asset.Colors.TrackableFilters.series.swiftUIColor,
				foreground: Asset.Colors.TrackableFilters.Text.onSeries.swiftUIColor
			)
		case .game:
			TrackableFilterView.Configuration(
				primarySystemImage: "numbersign",
				background: Asset.Colors.TrackableFilters.game.swiftUIColor,
				foreground: Asset.Colors.TrackableFilters.Text.onGame.swiftUIColor
			)
		}
	}
}

extension TrackableFilter.Sources {
	var primaryTitle: String {
		if let game {
			Strings.Game.titleWithOrdinal(game.index + 1)
		} else if let series {
			series.date.longFormat
		} else if let league {
			league.name
		} else {
			bowler.name
		}
	}

	var subtitles: [String] {
		if game != nil {
			[bowler.name, league?.name, series?.date.longFormat].compactMap { $0 }
		} else if series != nil {
			[bowler.name, league?.name].compactMap { $0 }
		} else if league != nil {
			[bowler.name]
		} else {
			[]
		}
	}
}

#Preview {
	let list: [(TrackableFilter, TrackableFilter.Sources)] = [
		(
			TrackableFilter(
				source: .bowler(UUID(0)),
				seriesFilter: .init(startDate: Date(), endDate: Date())
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil,
				series: nil,
				game: nil
			)
		),
		(
			TrackableFilter(
				source: .league(UUID(0)),
				seriesFilter: .init(startDate: Date())
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: nil,
				game: nil
			)
		),
		(
			TrackableFilter(
				source: .series(UUID(0)),
				seriesFilter: .init(endDate: Date())
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: .init(id: UUID(0), date: Date()),
				game: nil
			)
		),
		(
			TrackableFilter(
				source: .game(UUID(0))
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: .init(id: UUID(0), date: Date()),
				game: .init(id: UUID(0), index: 0, score: 450)
			)
		),
	]

	return List {
		ForEach(list, id: \.0) { item in
			Section {
				Button { } label: {
					TrackableFilterView(
						filter: item.0,
						sources: item.1
					)
				}
				.buttonStyle(.plain)
			}
			.listRowInsets(EdgeInsets())
		}
	}
}
