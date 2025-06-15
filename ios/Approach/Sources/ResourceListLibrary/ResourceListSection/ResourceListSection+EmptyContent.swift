import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ResourceListSectionEmptyContent: Equatable, Sendable {
	public let title: String
	public let message: String?
	public let action: String

	public init(
		title: String,
		message: String? = nil,
		action: String
	) {
		self.title = title
		self.message = message
		self.action = action
	}

	static let failedToLoad: Self = .init(
		title: Strings.Error.Generic.title,
		message: Strings.Error.loadingFailed,
		action: Strings.Action.reload
	)

	static let failedToDelete: Self = .init(
		title: Strings.Error.Generic.title,
		action: Strings.Action.reload
	)
}

@Reducer
public struct ResourceListSectionEmpty: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable, Sendable {
		public var content: ResourceListSectionEmptyContent
		public var style: Style

		public init(
			content: ResourceListSectionEmptyContent,
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
		@CasePathable
		public enum View {
			case didTapActionButton
		}
		@CasePathable
		public enum Internal { case doNothing }
		@CasePathable
		public enum Delegate {
			case didTapActionButton
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum Style: Sendable {
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

@ViewAction(for: ResourceListSectionEmpty.self)
public struct ResourceListSectionEmptyView: View {
	public let store: StoreOf<ResourceListSectionEmpty>

	public init(store: StoreOf<ResourceListSectionEmpty>) {
		self.store = store
	}

	public var body: some View {
		VStack(alignment: .center, spacing: 0) {
			Text(store.content.title)
				.font(.headline)

			if let message = store.content.message {
				Text(message)
					.multilineTextAlignment(.center)
					.padding(.top, .unitSpacing)
			}

			Button { send(.didTapActionButton) } label: {
				Text(store.content.action)
			}
			.padding(.top, .standardSpacing)
			.buttonStyle(.bordered)
		}
		.frame(maxWidth: .infinity)
	}
}

#Preview {
	List {
		Section {
			ResourceListSectionEmptyView(
				store: Store(
					initialState: ResourceListSectionEmpty.State(
						content: .failedToLoad,
						style: .error
					),
					reducer: { ResourceListSectionEmpty() }
				)
			)
		}
	}
}
