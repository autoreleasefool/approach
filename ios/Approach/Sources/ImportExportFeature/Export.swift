import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct Export: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var exportUrl: URL?
		public var lastExportAt: Date?
		public var daysSinceLastExport: DaysSince
		public var errorMessage: String?

		public init() {
			@Dependency(\.date) var date
			@Dependency(ExportService.self) var export
			self.lastExportAt = export.lastExportDate()
			self.daysSinceLastExport = export.lastExportDate()?.daysSince(date()) ?? .never
		}

		public var shareUrl: URL { exportUrl ?? URL(string: "https://tryapproach.app")! }
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didFirstAppear
			case didTapRetryButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didReceiveEvent(Result<ExportService.Event, Error>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public init() {}

	@Dependency(\.date) var  date
	@Dependency(ExportService.self) var export
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .merge(
						fetchExportData(&state),
						.run { _ in preferences.setDouble(forKey: .dataLastExportDate, to: date().timeIntervalSince1970) }
					)

				case .didTapRetryButton:
					return fetchExportData(&state)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didReceiveEvent(.success(event)):
					switch event {
					case .progress:
						return .none
					case let .response(url):
						state.exportUrl = url
						return .none
					}

				case let .didReceiveEvent(.failure(error)):
					state.exportUrl = nil
					state.errorMessage = error.localizedDescription
					return .none
				}

			case .delegate:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.didReceiveEvent(.success)):
				return Analytics.Data.Exported()
			default:
				return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didReceiveEvent(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	private func fetchExportData(_ state: inout State) -> Effect<Action> {
		state.errorMessage = nil
		return .run { send in
			for try await event in export.exportDatabase() {
				await send(.internal(.didReceiveEvent(.success(event))))
			}
		} catch: { error, send in
			await send(.internal(.didReceiveEvent(.failure(error))))
		}
	}
}

// MARK: - View

@ViewAction(for: Export.self)
public struct ExportView: View {
	public let store: StoreOf<Export>

	public init(store: StoreOf<Export>) {
		self.store = store
	}

	public var body: some View {
		VStack(spacing: 0) {
			List {
				Section {
					Text(Strings.Export.exportAnytime)
				}

				Section {
					Text(Strings.Export.weRecommend)
				}

				Section {
					HStack(spacing: .standardSpacing) {
						let (warningSymbol, warningSymbolColor) = store.daysSinceLastExport.warningSymbol()
						Image(systemSymbol: warningSymbol)
							.foregroundStyle(warningSymbolColor)

						Group {
							switch store.daysSinceLastExport {
							case .never:
								Text(Strings.Export.neverExported)
							case .days:
								if let lastExportAt = store.lastExportAt {
									Text(Strings.Export.lastExportedAt(lastExportAt.longFormat))
								} else {
									Text(Strings.Export.neverExported)
								}
							}
						}
						.frame(maxWidth: .infinity, alignment: .leading)
					}
				}

				Section {
					Text(Strings.Export.yourData)
				}
			}

			Divider()

			exportButton
			errorView
		}
		.navigationTitle(Strings.Export.title)
		.onFirstAppear { send(.didFirstAppear) }
		.onAppear { send(.onAppear) }
	}

	private var exportButton: some View {
		ShareLink(item: store.shareUrl) {
			Text(Strings.Export.exportData)
				.frame(maxWidth: .infinity)
		}
		.disabled(store.exportUrl == nil)
		.modifier(PrimaryButton())
		.padding()
	}

	@ViewBuilder private var errorView: some View {
		if let error = store.errorMessage {
			VStack {
				Text(Strings.Export.errorMessage(error))
					.foregroundColor(Asset.Colors.Error.default)

				Button(Strings.Action.tryAgain) {
					send(.didTapRetryButton)
				}
				.buttonStyle(.borderless)
			}
			.padding(.horizontal)
			.padding(.bottom)
		}
	}
}

#if DEBUG
struct ExportPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			ExportView(store: .init(
				initialState: Export.State(),
				reducer: Export.init
			))
		}
	}
}
#endif
