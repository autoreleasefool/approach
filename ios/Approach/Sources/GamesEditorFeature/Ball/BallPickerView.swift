import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

struct BallPickerView: View {
	@Binding var ballRolled: Gear.Summary?

	var body: some View {
		Button { } label: {
			HStack(alignment: .center, spacing: .standardSpacing) {
//				Color.red
//					.frame(width: .standardIcon, height: .standardIcon)
//					.cornerRadius(.standardIcon)

				VStack(alignment: .leading, spacing: .unitSpacing) {
					Text(Strings.Ball.Properties.ballRolled)
					Text(ballRolled?.name ?? Strings.none)
						.font(.caption)
				}

				Image(systemName: "chevron.up.chevron.down")
					.resizable()
					.frame(width: .tinyIcon, height: .tinyIcon)
					.foregroundColor(.appAction)
			}
			.contentShape(Rectangle())
		}
		.buttonStyle(TappableElement())
	}
}

#if DEBUG
struct BallPickerViewPreviews: PreviewProvider {
	static var previews: some View {
		BallPickerView(ballRolled: .constant(nil))
	}
}
#endif
