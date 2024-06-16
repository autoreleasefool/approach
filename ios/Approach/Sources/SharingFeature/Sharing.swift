import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct Sharing: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let source: Source

		public var seriesSharing: SeriesSharing.State?
		public var statisticsSharing: StatisticsWidgetSharing.State?
		public var gamesSharing: GamesSharing.State?

		public var tabs: [SharingTab]
		public var selectedTab: SharingTab

		public var isPreviewingImage: Bool = false
		public var preview: Preview?

		public init(source: Source) {
			self.source = source

			switch source {
			case let .series(seriesId):
				seriesSharing = .init(seriesId: seriesId) // TODO: We can also eventually instantiate GamesSharing here
				tabs = [.series]
				selectedTab = .series
			case let .statistic(source, statistic):
				statisticsSharing = .init(source: source, statistic: statistic)
				tabs = [.statistic]
				selectedTab = .statistic
			case let .games(seriesId):
				gamesSharing = .init(seriesId: seriesId)
				tabs = [.games]
				selectedTab = .games
			}
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didTapSaveButton
			case didTapShareButton
			case didTapDoneButton
			case didTapPreviewImage
			case didTapBackdrop
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case seriesSharing(SeriesSharing.Action)
			case statisticsSharing(StatisticsWidgetSharing.Action)
			case gamesSharing(GamesSharing.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum Source: Equatable {
		case games(Series.ID)
		case series(Series.ID)
		case statistic(StatisticsWidget.Source?, statistic: String?)
	}

	public enum SharingTab: Equatable {
		case series
		case games
		case statistic
	}

	public struct Preview: Equatable {
		public let preview: UIImage
		public let image: Image
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

				case .didTapPreviewImage:
					state.isPreviewingImage = true
					return .none

				case .didTapBackdrop:
					state.isPreviewingImage = false
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .seriesSharing(.delegate(.imageRendered(image))):
					state.preview = Preview(preview: image, image: Image(uiImage: image))
					return .none

				case let .statisticsSharing(.delegate(.imageRendered(image))):
					state.preview = Preview(preview: image, image: Image(uiImage: image))
					return .none

				case let .gamesSharing(.delegate(.imageRendered(image))):
					state.preview = Preview(preview: image, image: Image(uiImage: image))
					return .none

				case .statisticsSharing(.binding), .statisticsSharing(.internal), .statisticsSharing(.view),
						.seriesSharing(.binding), .seriesSharing(.internal), .seriesSharing(.view),
						.gamesSharing(.binding), .gamesSharing(.internal), .gamesSharing(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.seriesSharing, action: \.internal.seriesSharing) {
			SeriesSharing()
		}
		.ifLet(\.statisticsSharing, action: \.internal.statisticsSharing) {
			StatisticsWidgetSharing()
		}
		.ifLet(\.gamesSharing, action: \.internal.gamesSharing) {
			GamesSharing()
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
	@Namespace private var previewNamespace
	public var store: StoreOf<Sharing>

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		ZStack {
			VStack(spacing: 0) {
				if store.tabs.count > 1 {
					tabs
				} else {
					switch store.selectedTab {
					case .series:
						seriesSharing
					case .statistic:
						statisticsSharing
					case .games:
						gamesSharing
					}
				}

				VStack(spacing: 0) {
					Divider()

					if !store.isPreviewingImage {
						previewImage
							.matchedGeometryEffect(id: "Preview", in: previewNamespace)
							.padding(.top)
							.padding(.horizontal)
					}

					shareButton
						.padding()
				}
			}
			.blur(radius: store.isPreviewingImage ? .largeRadius : 0)
			.onTapGesture { send(.didTapBackdrop, animation: .easeInOut) }
			.disabled(store.isPreviewingImage)
			.zIndex(1)

			if let preview = store.preview?.preview, store.isPreviewingImage {
				ModalImagePreview(image: preview, namespace: previewNamespace) {
					send(.didTapBackdrop, animation: .easeInOut)
				}
				.zIndex(2)
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
	private var statisticsSharing: some View {
		if let store = store.scope(state: \.statisticsSharing, action: \.internal.statisticsSharing) {
			StatisticsWidgetSharingView(store: store)
		}
	}

	@ViewBuilder
	private var gamesSharing: some View {
		if let store = store.scope(state: \.gamesSharing, action: \.internal.gamesSharing) {
			GamesSharingView(store: store)
		}
	}

	@ViewBuilder
	private var previewImage: some View {
		if let preview = store.preview?.image {
			preview
				.resizable()
				.frame(maxWidth: .infinity)
				.aspectRatio(contentMode: .fit)
				.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
				.onTapGesture { send(.didTapPreviewImage, animation: .easeInOut) }
		}
	}

	@ViewBuilder
	private var shareButton: some View {
		if let image = store.preview?.image {
			ShareLink(item: image, preview: SharePreview(Strings.App.name, image: image)) {
				ShareImageButton()
			}
			.modifier(ShareImageButtonModifier())
		} else {
			Button { } label: {
				ShareImageButton()
			}
			.modifier(ShareImageButtonModifier())
			.disabled(true)
		}
	}
}

private struct ShareImageButton: View {
	var body: some View {
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
}

private struct ShareImageButtonModifier: ViewModifier {
	func body(content: Content) -> some View {
		content
			.buttonStyle(.borderedProminent)
			.controlSize(.large)
			.foregroundColor(Asset.Colors.Text.onPrimary)
			.tint(Asset.Colors.Primary.default)
	}
}
