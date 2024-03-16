import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ResourceListEmptyContent: Equatable {
	public let image: ImageAsset
	public let title: String
	public let message: String?
	public let action: String

	public init(image: ImageAsset, title: String, message: String? = nil, action: String) {
		self.image = image
		self.title = title
		self.message = message
		self.action = action
	}

	static let failedToLoad: Self = .init(
		image: Asset.Media.Error.notFound,
		title: Strings.Error.Generic.title,
		message: Strings.Error.loadingFailed,
		action: Strings.Action.reload
	)

	static let failedToDelete: Self = .init(
		image: Asset.Media.Error.notFound,
		title: Strings.Error.Generic.title,
		action: Strings.Action.reload
	)
}

@Reducer
public struct ResourceListEmpty: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var content: ResourceListEmptyContent
		public var style: Style

		public init(
			content: ResourceListEmptyContent,
			style: Style
		) {
			self.content = content
			self.style = style
		}

		public static let failedToLoad: Self = .init(
			content: .failedToLoad,
			style: .error
		)

		public static let failedToDelete: Self = .init(
			content: .failedToDelete,
			style: .error
		)
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didTapActionButton
		}
		@CasePathable public enum Internal { case doNothing }
		@CasePathable public enum Delegate {
			case didTapActionButton
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum Style {
		case empty
		case error
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapActionButton:
					return .send(.delegate(.didTapActionButton))
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: ResourceListEmpty.self)
public struct ResourceListEmptyView: View {
	public var store: StoreOf<ResourceListEmpty>

	public init(store: StoreOf<ResourceListEmpty>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			VStack {
				Spacer()

				store.content.image.swiftUIImage
					.resizable()
					.scaledToFit()
					.padding(.bottom, .smallSpacing)

				Spacer()

				VStack(spacing: .smallSpacing) {
					Text(store.content.title)
						.font(.headline)

					if let message = store.content.message {
						Text(message)
							.multilineTextAlignment(.center)
					}
				}
				.padding()
				.frame(maxWidth: .infinity)
				.background(store.style == .error ? Asset.Colors.Error.light : Asset.Colors.Primary.light)
				.cornerRadius(.standardRadius)
				.padding(.bottom, .smallSpacing)
				.layoutPriority(1)
			}

			Button {
				send(.didTapActionButton)
			} label: {
				Text(store.content.action)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
		}
		.padding()
	}
}
