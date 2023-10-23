import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Bowler {
	public struct ArchivedView: SwiftUI.View {
		let name: String
		let totalNumberOfLeagues: Int
		let totalNumberOfSeries: Int
		let totalNumberOfGames: Int

		public init(name: String, totalNumberOfLeagues: Int, totalNumberOfSeries: Int, totalNumberOfGames: Int) {
			self.name = name
			self.totalNumberOfLeagues = totalNumberOfLeagues
			self.totalNumberOfSeries = totalNumberOfSeries
			self.totalNumberOfGames = totalNumberOfGames
		}

		public init(_ bowler: Bowler.Archived) {
			self.init(
				name: bowler.name,
				totalNumberOfLeagues: bowler.totalNumberOfLeagues,
				totalNumberOfSeries: bowler.totalNumberOfSeries,
				totalNumberOfGames: bowler.totalNumberOfGames
			)
		}

		public var body: some SwiftUI.View {
			HStack {
				Image(systemSymbol: .personFill)

				VStack(alignment: .leading, spacing: .smallSpacing) {
					Text(name)
						.bold()
					Text(Strings.Archive.List.Bowlers.archivedWith(totalNumberOfLeagues, totalNumberOfSeries, totalNumberOfGames))
						.font(.caption2)
				}
			}
		}
	}
}

#if DEBUG
struct BowlerArchivedViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Bowler.ArchivedView(name: "Joseph", totalNumberOfLeagues: 2, totalNumberOfSeries: 8, totalNumberOfGames: 31)
		}
	}
}
#endif
