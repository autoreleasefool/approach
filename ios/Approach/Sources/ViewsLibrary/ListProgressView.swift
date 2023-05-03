import SwiftUI

public struct ListProgressView: View {
	public init() {}

	public var body: some View {
		Section {
			HStack {
				Spacer()
				ProgressView()
				Spacer()
			}
		}
	}
}
