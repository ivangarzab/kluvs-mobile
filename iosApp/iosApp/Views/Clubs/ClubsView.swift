import SwiftUI
import Shared

struct ClubsView: View {
    let userId: String
    @StateObject private var viewModel = ClubDetailsViewModelWrapper()
    @State private var selectedTab = 0
    @State private var showClubSelector = false

    var body: some View {
        ZStack {
            switch viewModel.screenState {
            case .loading:
                LoadingView()
                    .transition(.opacity)
            case .error(let message):
                ErrorView(message: message, onRetry: {
                    viewModel.loadUserClubs(userId: userId)
                })
                .transition(.opacity)
            case .empty:
                VStack(spacing: 8) {
                    Text(String(localized: "empty_no_clubs"))
                        .font(.title2)
                        .fontWeight(.semibold)

                    Text(String(localized: "empty_no_clubs_hint"))
                        .font(.body)
                        .foregroundColor(.secondary)
                }
                .transition(.opacity)
            case .content:
                VStack(spacing: 0) {
                    ClubSelectorRow(
                        clubName: viewModel.clubDetails?.clubName ?? "",
                        hasMultipleClubs: viewModel.availableClubs.count > 1,
                        onTap: { showClubSelector = true }
                    )

                    // Tab selector
                    Picker("", selection: $selectedTab) {
                        Text("tab_general").tag(0)
                        Text("tab_active_session").tag(1)
                        Text("tab_members").tag(2)
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    .tint(.brandOrange)
                    .padding(.horizontal)
                    .padding(.top, 8)

                    // Tab content
                    if viewModel.isLoading {
                        Spacer()
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle())
                            .scaleEffect(1.5)
                        Spacer()
                    } else {
                        TabView(selection: $selectedTab) {
                            GeneralTab(clubDetails: viewModel.clubDetails)
                                .tag(0)

                            ActiveSessionTab(sessionDetails: viewModel.activeSession)
                                .tag(1)

                            MembersTab(members: viewModel.members)
                                .tag(2)
                        }
                        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                    }
                }
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.availableClubs.isEmpty)
        .onAppear {
            viewModel.loadUserClubs(userId: userId)
        }
        .sheet(isPresented: $showClubSelector) {
            ClubSelectorSheet(
                clubs: viewModel.availableClubs,
                selectedClubId: viewModel.selectedClubId,
                onClubSelected: { clubId in
                    viewModel.selectClub(clubId: clubId)
                }
            )
        }
    }
}

#Preview {
    ClubsView(userId: "1")
}

// MARK: - Club Selector Row
private struct ClubSelectorRow: View {
    let clubName: String
    let hasMultipleClubs: Bool
    let onTap: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            if hasMultipleClubs {
                Image(systemName: "chevron.up.chevron.down")
                    .foregroundColor(.primary)
            }

            Text(clubName)
                .font(.headline)
                .foregroundColor(.primary)
                .frame(maxWidth: .infinity, alignment: .leading)
                .id(clubName)
                .transition(.asymmetric(
                    insertion: .move(edge: .bottom).combined(with: .opacity),
                    removal: .move(edge: .top).combined(with: .opacity)
                ))
                .animation(.easeInOut(duration: 0.3), value: clubName)

            // TODO: Impl once we have club/ create feature w/uiux
            // Image(systemName: "plus")
            //     .foregroundColor(.primary)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
        .onTapGesture {
            if hasMultipleClubs {
                onTap()
            }
        }
    }
}
