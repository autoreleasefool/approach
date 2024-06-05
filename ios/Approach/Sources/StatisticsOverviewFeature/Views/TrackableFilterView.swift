import AssetsLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

public struct TrackableFilterView: View {
	public let filter: TrackableFilter
	public let sources: TrackableFilter.Sources

	public var body: some View {
		HStack {
			Image(systemSymbol: filter.source.systemSymbol)

			VStack(alignment: .leading) {
				Text(sources.bowler.name)
					.font(.headline)
			}
		}
	}
}

extension TrackableFilter.Source {
	var systemSymbol: SFSymbol {
		switch self {
		case .bowler: .personFill
		case .league: .listBullet
		case .series: .calendar
		case .game: .numbersign
		}
	}
}

#Preview {
	let list: [(TrackableFilter, TrackableFilter.Sources)] = [
		(
			TrackableFilter(
				source: .bowler(UUID(0))
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: .init(id: UUID(0), date: Date()),
				game: .init(id: UUID(0), index: 0, score: 450)
			)
		),
		(
			TrackableFilter(
				source: .league(UUID(0))
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: .init(id: UUID(0), date: Date()),
				game: .init(id: UUID(0), index: 0, score: 450)
			)
		),
		(
			TrackableFilter(
				source: .series(UUID(0))
			),
			TrackableFilter.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2024"),
				series: .init(id: UUID(0), date: Date()),
				game: .init(id: UUID(0), index: 0, score: 450)
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
				.buttonStyle(.navigation)
			}
		}
	}
}
