import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct Sharing: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let source: Source

		public var seriesSharing: SeriesSharing.State?

		public var tabs: [SharingTab]
		public var selectedTab: SharingTab

		public var shareImage: Image?

		public init(source: Source) {
			self.source = source

			switch source {
			case let .series(seriesId):
				seriesSharing = .init(seriesId: seriesId)
				tabs = [.series]
				selectedTab = .series
			}
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didTapSaveButton
			case didTapShareButton
			case didTapDoneButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case seriesSharing(SeriesSharing.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum Source: Equatable {
		case series(Series.ID)
	}

	public enum SharingTab: Equatable {
		case series
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapSaveButton:
					return .none

				case .didTapShareButton:
					return .none

				case .didTapDoneButton:
					return .run { _ in await dismiss() }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .seriesSharing(.delegate(.imageRendered(image))):
					state.shareImage = Image(uiImage: image)
					return .none

				case .seriesSharing(.binding), .seriesSharing(.internal), .seriesSharing(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.seriesSharing, action: \.internal.seriesSharing) {
			SeriesSharing()
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: Sharing.self)
public struct SharingView: View {
	public var store: StoreOf<Sharing>

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		VStack(spacing: 0) {
			if store.tabs.count > 1 {
				tabs
			} else {
				switch store.selectedTab {
				case .series:
					seriesSharing
				}
			}

			VStack(spacing: 0) {
				Divider()

				shareButton
			}
		}
		.navigationTitle(Strings.Sharing.title)
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.done) { send(.didTapDoneButton) }
			}
		}
		.onAppear { send(.onAppear) }
	}

	private var tabs: some View {
		// TODO: Show tab picker
		EmptyView()
	}

	@ViewBuilder
	private var seriesSharing: some View {
		if let store = store.scope(state: \.seriesSharing, action: \.internal.seriesSharing) {
			SeriesSharingView(store: store)
		}
	}

	@ViewBuilder
	private var shareButton: some View {
		if let image = store.shareImage {
			ShareLink(item: image, preview: SharePreview(Strings.App.name, image: image)) {
				HStack(alignment: .center, spacing: .standardSpacing) {
					Spacer()

					Image(systemSymbol: .squareAndArrowUp)
						.resizable()
						.scaledToFit()
						.frame(width: .smallIcon, height: .smallIcon)

					Text(Strings.Action.share)
						.font(.subheadline)
						.fontWeight(.bold)

					Spacer()
				}
			}
			.buttonStyle(.borderedProminent)
			.controlSize(.large)
			.foregroundColor(Asset.Colors.Text.onPrimary)
			.tint(Asset.Colors.Primary.default)
			.padding()
		}
	}
}
