import AddressLookupServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct AddressLookupView: View {
	let store: StoreOf<AddressLookup>

	public init(store: StoreOf<AddressLookup>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				if viewStore.isLoadingAddress {
					Section {
						HStack(alignment: .center) {
							ProgressView()
						}
						.frame(maxWidth: .infinity)
					}
				} else {
					if let error = viewStore.loadingAddressError ?? viewStore.loadingResultsError {
						Section {
							Banner(.titleAndMessage(Strings.Error.Generic.title, error), style: .error)
						}
						.listRowInsets(EdgeInsets())
					}

					Section(Strings.List.results) {
						if viewStore.results.isEmpty {
							Text(Strings.Address.Error.Empty.title)
								.font(.caption)
								.multilineTextAlignment(.center)
						} else {
							ForEach(viewStore.results) { result in
								Button { viewStore.send(.didTapResult(result.id)) } label: {
									VStack(alignment: .leading, spacing: .tinySpacing) {
										Text(result.completion.wrapped.title)
											.font(.body)
											.foregroundColor(Color(uiColor: .label))
										if !result.completion.wrapped.subtitle.isEmpty {
											Text(result.completion.wrapped.subtitle)
												.font(.caption)
												.foregroundColor(Color(uiColor: .secondaryLabel))
										}
									}
								}
							}
						}
					}
				}
			}
			.searchable(
				text: viewStore.$query,
				prompt: Text(Strings.Action.search)
			)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.cancel) { viewStore.send(.didTapCancelButton) }
				}
			}
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}
