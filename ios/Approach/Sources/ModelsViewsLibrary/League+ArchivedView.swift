import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension League {
	public struct ArchivedView: SwiftUI.View {
		let name: String
		let bowlerName: String
		let totalNumberOfSeries: Int
		let totalNumberOfGames: Int

		public init(name: String, bowlerName: String, totalNumberOfSeries: Int, totalNumberOfGames: Int) {
			self.name = name
			self.bowlerName = bowlerName
			self.totalNumberOfSeries = totalNumberOfSeries
			self.totalNumberOfGames = totalNumberOfGames
		}

		public init(_ league: League.Archived) {
			self.init(
				name: league.name,
				bowlerName: league.bowlerName,
				totalNumberOfSeries: league.totalNumberOfSeries,
				totalNumberOfGames: league.totalNumberOfGames
			)
		}

		public var body: some SwiftUI.View {
			HStack {
				Image(systemSymbol: .repeat)

				VStack(alignment: .leading, spacing: .smallSpacing) {
					VStack(alignment: .leading, spacing: .tinySpacing) {
						Text(name)
							.bold()
						Text(Strings.Archive.List.Leagues.belongsTo(bowlerName))
							.font(.caption)
					}

					Text(Strings.Archive.List.Leagues.archivedWith(totalNumberOfSeries, totalNumberOfGames))
						.font(.caption2)
				}
			}
		}
	}
}

#if DEBUG
struct LeagueArchivedViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			League.ArchivedView(name: "Majors 23/24", bowlerName: "Joseph", totalNumberOfSeries: 8, totalNumberOfGames: 31)
		}
	}
}
#endif
