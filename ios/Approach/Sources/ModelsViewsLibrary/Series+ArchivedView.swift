import AssetsLibrary
import DateTimeLibrary
import Foundation
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Series {
	public struct ArchivedView: SwiftUI.View {
		let date: Date
		let bowlerName: String
		let leagueName: String
		let totalNumberOfGames: Int

		public init(date: Date, bowlerName: String, leagueName: String, totalNumberOfGames: Int) {
			self.date = date
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.totalNumberOfGames = totalNumberOfGames
		}

		public init(_ series: Series.Archived) {
			self.init(
				date: series.date,
				bowlerName: series.bowlerName,
				leagueName: series.leagueName,
				totalNumberOfGames: series.totalNumberOfGames
			)
		}

		public var body: some SwiftUI.View {
			HStack {
				Image(systemSymbol: .calendar)

				VStack(alignment: .leading, spacing: .smallSpacing) {
					VStack(alignment: .leading, spacing: .tinySpacing) {
						Text(date.longFormat)
							.bold()
						Text(Strings.Archive.List.Series.belongsTo(bowlerName, leagueName))
							.font(.caption)
					}

					Text(Strings.Archive.List.Series.archivedWith(totalNumberOfGames))
						.font(.caption2)
				}
			}
		}
	}
}

#if DEBUG
struct SeriesArchivedViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Series.ArchivedView(date: Date(), bowlerName: "Joseph", leagueName: "Majors 23/24", totalNumberOfGames: 31)
		}
	}
}
#endif
